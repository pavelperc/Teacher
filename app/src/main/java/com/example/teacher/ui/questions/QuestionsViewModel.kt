package com.example.teacher.ui.questions

import androidx.lifecycle.ViewModel
import com.example.teacher.data.CategoriesRepository
import com.example.teacher.domain.entities.Category
import kotlinx.coroutines.flow.*

class QuestionsViewModel : ViewModel() {

    data class QuestionWithStats(
        val question: String,
        val answer: String,
        val failed: Boolean,
        val learned: Boolean,
    )

    private val _learnedCount = MutableStateFlow(0)
    val learnedCount = _learnedCount.asStateFlow()

    val questionsCount get() = category.questions.size

    private val _progressPercent = MutableStateFlow(0)
    val progressPercent = _progressPercent.asStateFlow()

    private val categoriesRepository = CategoriesRepository()

    lateinit var category: Category
        private set

    private val _questionsWithStats = MutableStateFlow(listOf<QuestionWithStats>())
    val questionsWithStats = _questionsWithStats.asStateFlow()

    private var isLanguageSwapped = false

    fun swapLanguage() {
        isLanguageSwapped = !isLanguageSwapped
        updateQuestionsWithStats()
    }

    fun setCategoryId(categoryId: Int) {
        category = categoriesRepository.getCategoryById(categoryId)
        updateQuestionsWithStats()
    }

    private fun updateQuestionsWithStats() {
        _questionsWithStats.value = category.questions.map {
            if (isLanguageSwapped) {
                QuestionWithStats(it.russian, it.english, false, false)
            } else {
                QuestionWithStats(it.english, it.russian, false, false)
            }
        }
    }
}