package com.android.go.sopt.winey.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import com.android.go.sopt.winey.presentation.main.MainActivity
import com.android.go.sopt.winey.presentation.onboarding.OnBoardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            checkAccessTokenAndNavigate()
        }, DELAY_TIME)
    }

    private fun checkAccessTokenAndNavigate() {
        val accessToken = runBlocking { dataStoreRepository.getAccessToken().firstOrNull() }
        if (accessToken.isNullOrEmpty() || accessToken == "") {
            navigateToOnBoardingScreen()
        } else {
            navigateToMainScreen()
        }
    }

    private fun navigateToOnBoardingScreen() {
        Intent(this@SplashActivity, OnBoardingActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(this)
        }
        finish()
    }

    private fun navigateToMainScreen() {
        Intent(this@SplashActivity, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(this)
        }
        finish()
    }

    companion object {
        private const val DELAY_TIME = 1500L
    }
}
