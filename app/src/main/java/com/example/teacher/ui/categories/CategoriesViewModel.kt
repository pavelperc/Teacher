package com.example.teacher.ui.categories

import androidx.lifecycle.ViewModel
import com.example.teacher.data.CategoriesRepository

class CategoriesViewModel : ViewModel() {

    data class CategoryWithStats(
        val id: Int,
        val category: String,
        val percentage: String
    )

    val categoriesRepository = CategoriesRepository()

    val categories: List<CategoryWithStats>

    init {
        categories = categoriesRepository.getCategories()
            .map { CategoryWithStats(it.id, it.name, "") }
    }
}