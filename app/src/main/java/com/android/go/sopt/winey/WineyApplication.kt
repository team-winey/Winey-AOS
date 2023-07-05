package com.android.go.sopt.winey

import dagger.hilt.android.HiltAndroidApp
import android.app.Application

@HiltAndroidApp
class WineyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}