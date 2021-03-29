package com.example.teacher.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.teacher.TeacherApplication
import com.example.teacher.domain.QuestionStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Database(entities = arrayOf(QuestionStats::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionStatsDao(): QuestionStatsDao
    abstract fun categoryStatsDao(): CategoryStatsDao

    companion object {
        private var instance: AppDatabase? = null

        fun get() = instance ?: buildDatabase().also {
            instance = it
            it.questionStatsDao().apply { // prepopulate for debug
                GlobalScope.launch(Dispatchers.IO) {
                    insertOrUpdate(QuestionStats(0, 0, 1, 0))
                    insertOrUpdate(QuestionStats(0, 1, 2, 0))
                    insertOrUpdate(QuestionStats(0, 2, 0, 2))
                    insertOrUpdate(QuestionStats(0, 3, 2, 2))
                }
            }
        }

        private fun buildDatabase() =
            Room.databaseBuilder(
                TeacherApplication.get(),
                AppDatabase::class.java, "database"
            ).build()
    }
}