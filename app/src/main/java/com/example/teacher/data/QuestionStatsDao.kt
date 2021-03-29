package com.example.teacher.data

import androidx.room.*
import com.example.teacher.domain.QuestionStats
import kotlinx.coroutines.flow.Flow


@Dao
interface QuestionStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(questionStats: QuestionStats)


    @Query("select * from QuestionStats where categoryId=:categoryId")
    fun observeAllForCategory(categoryId: Int): Flow<List<QuestionStats>>
}