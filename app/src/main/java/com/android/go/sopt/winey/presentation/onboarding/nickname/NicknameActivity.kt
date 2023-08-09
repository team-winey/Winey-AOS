package com.android.go.sopt.winey.presentation.onboarding.nickname

import android.os.Bundle
import androidx.activity.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityNicknameBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.drawableOf

class NicknameActivity : BindingActivity<ActivityNicknameBinding>(R.layout.activity_nickname) {
    private val viewModel by viewModels<NicknameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel

        //initEditTextFocusListener()
    }

    private fun initEditTextFocusListener() {
        binding.etNickname.setOnFocusChangeListener { view, hasFocus ->
            view.background = if (hasFocus) {
                drawableOf(R.drawable.shape_purple_line_5_rect)
            } else {
                drawableOf(R.drawable.shape_gray_line_5_rect)
            }
        }
    }
}
