package com.example.teacher

import android.app.Application
import android.content.Context

class TeacherApplication : Application() {
    companion object {
        private lateinit var instance: TeacherApplication
        fun get() = instance

        fun preferences() = get().getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}