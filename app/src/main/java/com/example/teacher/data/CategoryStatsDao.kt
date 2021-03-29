package com.example.teacher.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryStatsDao {

    @Query("""
        select categoryId, count(*) as learnedCount from QuestionStats
        where correctCount > 0
        group by categoryId
        """)
    fun observeCategoryStats(): Flow<List<CategoryStatsDto>>

    data class CategoryStatsDto(
        val categoryId: Int,
        val learnedCount: Int
    )
}