package org.go.sopt.winey

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import org.go.sopt.winey.BuildConfig.KAKAO_NATIVE_KEY
import org.go.sopt.winey.util.activity.ActivityLifecycleHandler
import timber.log.Timber

@HiltAndroidApp
class WineyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupKakaoSdk()
        preventDarkMode()
        registerActivityLifecycleCallbacks(ActivityLifecycleHandler())
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun setupKakaoSdk() {
        KakaoSdk.init(this, KAKAO_NATIVE_KEY)
    }

    private fun preventDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
