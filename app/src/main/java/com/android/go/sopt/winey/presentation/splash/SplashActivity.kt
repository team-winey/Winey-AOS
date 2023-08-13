package com.android.go.sopt.winey.presentation.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivitySplashBinding
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import com.android.go.sopt.winey.presentation.main.MainActivity
import com.android.go.sopt.winey.presentation.onboarding.login.LoginActivity
import com.android.go.sopt.winey.presentation.onboarding.nickname.NicknameActivity
import com.android.go.sopt.winey.util.binding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Splash -> Onboarding -> Login -> 여기서 서버 오류 발생 -> Nickname -> Main
 *        -> 자동 로그인 -> Main
 * */
@AndroidEntryPoint
class SplashActivity : BindingActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(DELAY_TIME)
            //checkAutoLogin()
            navigateTo<NicknameActivity>()
        }
    }

    private fun checkAutoLogin() {
        val accessToken = runBlocking { dataStoreRepository.getAccessToken().firstOrNull() }
        if (accessToken.isNullOrBlank()) {
            navigateTo<LoginActivity>()
        } else {
            navigateTo<MainActivity>()
        }
    }

    private inline fun <reified T : Activity> navigateTo() {
        Intent(this@SplashActivity, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    companion object {
        private const val DELAY_TIME = 1500L
    }
}
