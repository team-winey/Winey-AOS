package com.go.sopt.winey.presentation.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.ActivitySplashBinding
import com.go.sopt.winey.domain.repository.DataStoreRepository
import com.go.sopt.winey.presentation.main.MainActivity
import com.go.sopt.winey.presentation.onboarding.guide.GuideActivity
import com.go.sopt.winey.util.binding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BindingActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(DELAY_TIME)
            checkAutoLogin()
        }
    }

    private fun checkAutoLogin() {
        val accessToken = runBlocking { dataStoreRepository.getAccessToken().firstOrNull() }
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
