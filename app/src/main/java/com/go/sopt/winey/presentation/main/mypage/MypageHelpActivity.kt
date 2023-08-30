package com.go.sopt.winey.presentation.main.mypage

import android.os.Bundle
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.ActivityMypageHelpBinding
import com.go.sopt.winey.util.binding.BindingActivity

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
