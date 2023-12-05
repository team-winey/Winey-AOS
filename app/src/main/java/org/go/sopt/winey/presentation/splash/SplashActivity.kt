package org.go.sopt.winey.presentation.splash

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivitySplashBinding
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.presentation.onboarding.guide.GuideActivity
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.colorOf
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

    private suspend fun checkAutoLogin() {
        val accessToken = dataStoreRepository.getAccessToken().firstOrNull()
        if (accessToken.isNullOrBlank()) {
            navigateTo<GuideActivity>()
        } else {
            navigateToMainScreen()
        }
    }

    private inline fun <reified T : Activity> navigateTo() {
        Intent(this, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    private fun navigateToMainScreen() {
        Intent(this, MainActivity::class.java).apply {
            if (intent.extras != null) {
                putExtra(KEY_NOTI_TYPE, intent.getStringExtra(KEY_NOTI_TYPE))
                putExtra(KEY_FEED_ID, intent.getStringExtra(KEY_FEED_ID))
            }
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    companion object {
        private const val KEY_FEED_ID = "feedId"
        private const val KEY_NOTI_TYPE = "notiType"
        private const val DELAY_TIME = 1500L
    }
}
