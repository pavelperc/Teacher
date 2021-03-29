package com.example.teacher.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacher.data.CategoriesRepository
import com.example.teacher.domain.CategoryWithStats
import com.example.teacher.domain.QuestionWithStats
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    val categoriesRepository = CategoriesRepository()

    data class HeaderViewState(
        val categoryName: String,
        val taskRatioStr: String
    )

    sealed class QuizViewState {
        data class Question(
            val question: String,
            val answer1: String,
            val answer2: String,
            val answer3: String,
            val answer4: String
        ) : QuizViewState()

        data class Answered(
            val question: String,
            val answer: String,
            val isCorrect: Boolean
        ) : QuizViewState()

        object Empty : QuizViewState()

        object Finish : QuizViewState()
    }

    private class QuizTask(
        val question: QuestionWithStats,
        val answer1: QuestionWithStats,
        val answer2: QuestionWithStats,
        val answer3: QuestionWithStats,
        val answer4: QuestionWithStats
    )

    private val _viewState = MutableStateFlow<QuizViewState>(QuizViewState.Empty)
    val quizViewState = _viewState.asStateFlow()

    private var isLanguageSwapped = false

    private val _headerViewState = MutableStateFlow(HeaderViewState("", ""))
    val headerViewState = _headerViewState.asStateFlow()

    private var categoryWithStats: CategoryWithStats? = null
        set(value) {
            field = value
            updateHeaderState()
        }

    private var quizTasks: List<QuizTask> = emptyList()
        set(value) {
            field = value
            updateHeaderState()
        }

    private var currentTaskIndex = 0
        set(value) {
            field = value
            updateHeaderState()
        }

    private val currentTask get() = quizTasks[currentTaskIndex]

    private fun updateHeaderState() {
        _headerViewState.value = HeaderViewState(
            categoryWithStats?.name ?: "",
            "${currentTaskIndex + 1} / ${quizTasks.size}"
        )
    }

    fun initWithCategoryAndSettings(categoryId: Int, isLanguageSwapped: Boolean) {
        if (categoryWithStats != null) return

        this.isLanguageSwapped = isLanguageSwapped

        viewModelScope.launch {
            categoryWithStats = categoriesRepository.observeCategoryWithStats(categoryId).first()
            prepareQuiz(categoryWithStats!!)
            updateQuizQuestion()
        }
    }

    fun answer1() = answer(currentTask.answer1)
    fun answer2() = answer(currentTask.answer2)
    fun answer3() = answer(currentTask.answer3)
    fun answer4() = answer(currentTask.answer4)

    private fun answer(answer: QuestionWithStats) {
        val isCorrect = answer == currentTask.question
        _viewState.value = QuizViewState.Answered(
            question = currentTask.question.question.run { if (isLanguageSwapped) english else russian },
            answer = answer.question.run { if (isLanguageSwapped) russian else english },
            isCorrect = isCorrect
        )
        viewModelScope.launch {
            val stats = currentTask.question.stats
            if (isCorrect) {
                categoriesRepository.updateStats(stats.copy(correctCount = stats.correctCount + 1))
            } else {
                categoriesRepository.updateStats(stats.copy(failedCount = stats.failedCount + 1))
            }
        }
    }

    fun goNext() {
        if (currentTaskIndex == quizTasks.size - 1) {
            _viewState.value = QuizViewState.Finish
            return
        }
        currentTaskIndex += 1
        updateQuizQuestion()
    }

    private fun prepareQuiz(categoryWithStats: CategoryWithStats) {
        val (notLearnedQuestions, learnedQuestions) = categoryWithStats.questions
            .partition { it.stats.correctCount == 0 }

        if (notLearnedQuestions.isEmpty() || categoryWithStats.questions.size < 4) {
            _viewState.value = QuizViewState.Finish
            return
        }

        val questionsToAsk = notLearnedQuestions.shuffled().take(10)
        val questionsForAlternatives =
            if (notLearnedQuestions.size >= 4) notLearnedQuestions
            else notLearnedQuestions + learnedQuestions.take(4 - notLearnedQuestions.size)

        quizTasks = questionsToAsk.map { question ->
            val alternatives = questionsForAlternatives.shuffled()
                .filter { it != question }
                .take(3)
                .plus(question)
                .shuffled()
            QuizTask(question, alternatives[0], alternatives[1], alternatives[2], alternatives[3])
        }
        currentTaskIndex = 0
    }

    private fun updateQuizQuestion() {
        _viewState.value = QuizViewState.Question(
            question = currentTask.question.question.run { if (isLanguageSwapped) english else russian },
            answer1 = currentTask.answer1.question.run { if (isLanguageSwapped) russian else english },
            answer2 = currentTask.answer2.question.run { if (isLanguageSwapped) russian else english },
            answer3 = currentTask.answer3.question.run { if (isLanguageSwapped) russian else english },
            answer4 = currentTask.answer4.question.run { if (isLanguageSwapped) russian else english }
        )
    }
}