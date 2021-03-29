package com.example.teacher.ui.questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacher.TeacherApplication
import com.example.teacher.data.CategoriesRepository
import com.example.teacher.domain.CategoryWithStats
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QuestionsViewModel : ViewModel() {

    data class QuestionViewState(
        val question: String,
        val answer: String,
        val status: QuestionStatus
    ) {
        val failed get() = status == QuestionStatus.FAILED
        val learned get() = status == QuestionStatus.LEARNED
    }

    enum class QuestionStatus {
        NOT_STARTED, FAILED, LEARNED
    }

    data class CategoryViewState(
        val categoryName: String,
        val learnedCount: Int,
        val questionsCount: Int,
    ) {
        val learnedPercent =
            if (questionsCount == 0) 0
            else learnedCount * 100 / questionsCount

        val learnedPercentStr = "$learnedPercent%"
    }

    private val categoriesRepository = CategoriesRepository()

    private val categoryWithStats = MutableStateFlow<CategoryWithStats?>(null)

    val categoryId get() = categoryWithStats.value?.id ?: throw IllegalStateException("no category")

    val categoryViewState = categoryWithStats
        .filterNotNull()
        .map {
            CategoryViewState(
                categoryName = it.name,
                learnedCount = it.learnedCount,
                questionsCount = it.questionsCount
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, CategoryViewState("", 0, 0))


    private val _questionViewStates = MutableStateFlow<List<QuestionViewState>>(emptyList())
    val questionViewStates = _questionViewStates.asStateFlow()

    private val sharedPreferences = TeacherApplication.preferences()

    var isLanguageSwapped = sharedPreferences.getBoolean("is_language_swapped", false)
        private set(value) {
            field = value
            sharedPreferences.edit().putBoolean("is_language_swapped", value).apply()
        }

    fun swapLanguage() {
        isLanguageSwapped = !isLanguageSwapped
        updateQuestionViewStates()
    }

    fun initWithCategoryId(categoryId: Int) {
        if (categoryWithStats.value != null) return

        viewModelScope.launch {
            categoriesRepository.observeCategoryWithStats(categoryId).collect {
                categoryWithStats.value = it
                updateQuestionViewStates()
            }
        }
    }

    private fun updateQuestionViewStates() {
        _questionViewStates.value = categoryWithStats.value?.questions?.map { (question, stats) ->
            val status = when {
                stats.correctCount > 0 -> QuestionStatus.LEARNED
                stats.failedCount > 0 -> QuestionStatus.FAILED
                else -> QuestionStatus.NOT_STARTED
            }

            if (isLanguageSwapped) {
                QuestionViewState(
                    question = question.russian,
                    answer = question.english,
                    status = status
                )
            } else {
                QuestionViewState(
                    question = question.english,
                    answer = question.russian,
                    status = status
                )
            }
        } ?: emptyList()
    }

    val hasNotLearnedQuestions
        get() = categoryWithStats.value?.questions?.count { it.stats.correctCount == 0 }
            ?.let { it > 0 } ?: true

    suspend fun resetLearnedQuestions() {
        val categoryId = categoryWithStats.value?.id ?: return
        viewModelScope.launch {
            categoriesRepository.deleteAllInCategory(categoryId)
        }
    }
}