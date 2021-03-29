package com.example.teacher.data

import com.example.teacher.R
import com.example.teacher.TeacherApplication
import com.example.teacher.domain.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class CategoriesRepository {
    companion object {
        private var categoriesCache: List<Category>? = null
    }

    private val questionStatsDao = AppDatabase.get().questionStatsDao()
    private val categoryStatsDao = AppDatabase.get().categoryStatsDao()

    fun getCategories() = categoriesCache ?: loadCategories().also { categoriesCache = it }

    fun getCategoryById(categoryId: Int) = getCategories()[categoryId]

    private fun loadCategories(): List<Category> {
        val context = TeacherApplication.get()
        val inputStream = context.resources.openRawResource(R.raw.questions)

        val table = inputStream
            .use { csvReader().readAll(it) }
            .drop(1)

        var currentCategory = ""
        return table
            .groupBy { (cat, _, _) ->
                if (cat.isNotBlank()) {
                    currentCategory = cat
                }
                currentCategory
            }
            .entries.mapIndexed { index, (name, questionRows) ->
                val questions = questionRows.mapIndexed { questionIndex, (_, eng, rus) ->
                    Question(questionIndex, eng, rus)
                }
                Category(index, name, questions)
            }
    }

    fun observeCategoryWithStats(categoryId: Int): Flow<CategoryWithStats> =
        questionStatsDao.observeAllForCategory(categoryId).map { statsList ->
            val statsById = statsList.associateBy { it.questionId }

            val category = getCategoryById(categoryId)
            val questionsWithStats = category.questions.map { question ->
                val stats = statsById[question.id]
                    ?: QuestionStats(category.id, question.id)
                QuestionWithStats(question, stats)
            }
            CategoryWithStats(category.id, category.name, questionsWithStats)
        }

    fun observeCategoryDescriptions(): Flow<List<CategoryDescription>> =
        categoryStatsDao.observeCategoryStats().map { categoryStatsList ->
            val categoryStatsById = categoryStatsList.associateBy { it.categoryId }
            getCategories().map { category ->
                val stats = categoryStatsById[category.id]
                    ?: CategoryStatsDao.CategoryStatsDto(category.id, 0)

                CategoryDescription(
                    categoryId = category.id,
                    categoryName = category.name,
                    learnedPercent = stats.learnedCount * 100 / category.questionsCount
                )
            }
        }

    suspend fun updateStats(questionStats: QuestionStats) {
        questionStatsDao.insertOrUpdate(questionStats)
    }

    suspend fun deleteAllInCategory(categoryId: Int) {
        questionStatsDao.deleteAllInCategory(categoryId)
    }
}