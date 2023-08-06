package com.android.go.sopt.winey.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityOnBoardingBinding
import com.android.go.sopt.winey.presentation.main.MainActivity
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnBoardingActivity :
    BindingActivity<ActivityOnBoardingBinding>(R.layout.activity_on_boarding) {
    val viewModel: OnBoardingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKakaoLoginButtonClickListener()
        loginObserver()
    }

    private fun initKakaoLoginButtonClickListener() {
        binding.btnOnboardingKakao.setOnClickListener {
            viewModel.kakaoLogin()
        }
    }

    private fun loginObserver() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->

                when (state) {
                    is UiState.Loading -> {
                    }

                    is UiState.Success -> {
                        val intent = Intent(this@OnBoardingActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    is UiState.Failure -> {

                    }

                    is UiState.Empty -> {
                    }
                }
            }
        }
    }
}
