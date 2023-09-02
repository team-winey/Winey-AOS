package org.go.sopt.winey.presentation.main.mypage

import android.os.Bundle
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityMypageHelpBinding
import org.go.sopt.winey.util.binding.BindingActivity

class MypageHelpActivity :
    BindingActivity<ActivityMypageHelpBinding>(R.layout.activity_mypage_help) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBtnEvent()
    }

    private fun initBtnEvent() {
        binding.btnHelpBack.setOnClickListener {
            finish()
        }

        binding.btnHelpClose.setOnClickListener {
            finish()
        }
    }
}
