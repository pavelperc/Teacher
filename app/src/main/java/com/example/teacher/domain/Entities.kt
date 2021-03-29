package com.example.teacher.domain

import androidx.room.Entity

data class Category(
    val id: Int,
    val name: String,
    val questions: List<Question>
) {
    val questionsCount get() = questions.size
}

data class Question(
    val id: Int,
    val english: String,
    val russian: String
)

@Entity(primaryKeys = arrayOf("categoryId", "questionId"))
data class QuestionStats(
    val categoryId: Int,
    val questionId: Int,
    val failedCount: Int = 0,
    val correctCount: Int = 0
)

/** Light description for main menu. */
data class CategoryDescription(
    val categoryId: Int,
    val categoryName: String,
    val learnedPercent: Int
) {
    val learnedPercentStr get() = "$learnedPercent%"
}

class CategoryWithStats(
    val id: Int,
    val name: String,
    val questions: List<QuestionWithStats>
) {
    val learnedCount = questions.count { it.stats.correctCount > 0 }
    val questionsCount = questions.size
}

data class QuestionWithStats(
    val question: Question,
    val stats: QuestionStats
)