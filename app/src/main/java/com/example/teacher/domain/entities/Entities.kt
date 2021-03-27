package com.example.teacher.domain.entities




data class Category(
    val name: String,
    val questions: List<Question>
)

data class Question(
    val english: String,
    val russian: String
)