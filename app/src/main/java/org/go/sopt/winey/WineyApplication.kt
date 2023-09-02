package org.go.sopt.winey

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import org.go.sopt.winey.BuildConfig.KAKAO_NATIVE_KEY
import timber.log.Timber

@HiltAndroidApp
class WineyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        KakaoSdk.init(this, KAKAO_NATIVE_KEY)
    }
}
