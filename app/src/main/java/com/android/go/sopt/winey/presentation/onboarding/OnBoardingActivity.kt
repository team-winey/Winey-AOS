package com.android.go.sopt.winey.presentation.onboarding

import android.os.Bundle
import androidx.activity.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityOnBoardingBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity :
    BindingActivity<ActivityOnBoardingBinding>(R.layout.activity_on_boarding) {
    val viewModel: OnBoardingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKakaoLoginButtonClickListener()
    }

    private fun initKakaoLoginButtonClickListener(){
        binding.btnOnboardingKakao.setOnClickListener {
            viewModel.kakaoLogin()
        }
    }
}
