package com.example.teacher.data

import com.example.teacher.R
import com.example.teacher.TeacherApplication
import com.example.teacher.domain.entities.Category
import com.example.teacher.domain.entities.Question
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader


class CategoriesRepository {
    companion object {
        private var categoriesCache: List<Category>? = null
    }

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
}