package com.android.go.sopt.winey.presentation.onboarding.nickname

import android.os.Bundle
import androidx.activity.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityNicknameBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.hideKeyboard

class NicknameActivity : BindingActivity<ActivityNicknameBinding>(R.layout.activity_nickname) {
    private val viewModel by viewModels<NicknameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel

        initRootLayoutClickListener()
    }

    private fun initRootLayoutClickListener() {
        binding.root.setOnClickListener {
            hideKeyboard(binding.root)
            binding.etNickname.clearFocus()
        }
    }
}
