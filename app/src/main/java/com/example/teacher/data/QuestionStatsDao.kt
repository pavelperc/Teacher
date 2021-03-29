package com.example.teacher.data

import androidx.room.*
import com.example.teacher.domain.QuestionStats
import kotlinx.coroutines.flow.Flow


@Dao
interface QuestionStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(questionStats: QuestionStats)

    @Query("delete from QuestionStats where categoryId = :categoryId")
    suspend fun deleteAllInCategory(categoryId: Int)

    @Query("select * from QuestionStats where categoryId=:categoryId")
    fun observeAllForCategory(categoryId: Int): Flow<List<QuestionStats>>
}