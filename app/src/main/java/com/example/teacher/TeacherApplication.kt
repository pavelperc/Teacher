package com.example.teacher

import android.app.Application

class TeacherApplication : Application() {
    companion object {
        private lateinit var instance: TeacherApplication
        fun get() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}