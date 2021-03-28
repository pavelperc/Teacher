package com.example.teacher.domain.entities




data class Category(
    val id: Int,
    val name: String,
    val questions: List<Question>
)

data class Question(
    val id: Int,
    val english: String,
    val russian: String
)