package com.android.go.sopt.winey.presentation.main.mypage

import android.os.Bundle
import androidx.activity.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityMypageHelpBinding
import com.android.go.sopt.winey.util.binding.BindingActivity

class MypageHelpActivity :
    BindingActivity<ActivityMypageHelpBinding>(R.layout.activity_mypage_help) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBtnEvent()
    }

    fun initBtnEvent() {
        binding.btnHelpBack.setOnClickListener {
            finish()
        }

        binding.btnHelpClose.setOnClickListener {
            finish()
        }
    }
}