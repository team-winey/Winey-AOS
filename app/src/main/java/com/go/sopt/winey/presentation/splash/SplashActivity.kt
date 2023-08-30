package com.go.sopt.winey.presentation.splash

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.ActivitySplashBinding
import com.go.sopt.winey.domain.repository.DataStoreRepository
import com.go.sopt.winey.presentation.main.MainActivity
import com.go.sopt.winey.presentation.onboarding.guide.GuideActivity
import com.go.sopt.winey.util.binding.BindingActivity
import com.go.sopt.winey.util.context.colorOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BindingActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setUpStatusBar()
        lifecycleScope.launch {
            delay(DELAY_TIME)
            checkAutoLogin()
        }
    }

    private fun setUpStatusBar() {
        // 상태바 색상 변경
        window.statusBarColor = colorOf(R.color.black)

        // 상태바에 있는 아이콘 밝게 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
            windowInsetController?.isAppearanceLightStatusBars = false
        }
    }

    @Suppress("DEPRECATION")
    fun setLightStatusBar(view: View, isLight: Boolean) {
        var flags = view.systemUiVisibility
        flags = if (isLight) {
            flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        view.systemUiVisibility = flags
    }

    private suspend fun checkAutoLogin() {
        val accessToken = dataStoreRepository.getAccessToken().firstOrNull()
        if (accessToken.isNullOrBlank()) {
            navigateTo<GuideActivity>()
        } else {
            navigateTo<MainActivity>()
        }
    }

    private inline fun <reified T : Activity> navigateTo() {
        Intent(this, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    companion object {
        private const val DELAY_TIME = 1500L
    }
}
