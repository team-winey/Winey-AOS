package org.go.sopt.winey

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import org.go.sopt.winey.BuildConfig.KAKAO_NATIVE_KEY
import timber.log.Timber

@HiltAndroidApp
class WineyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupKakaoSdk()
        preventDarkMode()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: android.app.Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: android.app.Activity) {}

            override fun onActivityResumed(activity: android.app.Activity) {
                isAppInForeground = true
            }

            override fun onActivityPaused(activity: android.app.Activity) {
                isAppInForeground = false
            }

            override fun onActivityStopped(activity: android.app.Activity) {}

            override fun onActivitySaveInstanceState(activity: android.app.Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: android.app.Activity) {}
        })
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

    companion object {
        var isAppInForeground = false
    }
}
