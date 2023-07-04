package com.android.go.sopt.winey

import dagger.hilt.android.HiltAndroidApp
import android.app.Application

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}