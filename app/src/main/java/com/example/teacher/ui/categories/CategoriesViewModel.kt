package com.example.teacher.ui.categories

import androidx.lifecycle.ViewModel
import com.example.teacher.data.CategoriesRepository

class CategoriesViewModel : ViewModel() {

    private val categoriesRepository = CategoriesRepository()


    val categoryDescriptions
        get() = categoriesRepository.observeCategoryDescriptions()
}