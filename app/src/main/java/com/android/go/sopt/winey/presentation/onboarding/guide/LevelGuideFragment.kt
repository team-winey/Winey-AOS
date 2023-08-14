package com.android.go.sopt.winey.presentation.onboarding.guide

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentLevelGuideBinding
import com.android.go.sopt.winey.presentation.onboarding.login.LoginActivity
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.fragment.colorOf

class LevelGuideFragment :
    BindingFragment<FragmentLevelGuideBinding>(R.layout.fragment_level_guide) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateStatusBarColor()
        initStartButtonClickListener()
    }

    private fun updateStatusBarColor() {
        requireActivity().window.statusBarColor = colorOf(R.color.white)
    }

    private fun initStartButtonClickListener() {
        binding.btnGuideStart.setOnClickListener {
            navigateToLoginScreen()
        }
    }

    private fun navigateToLoginScreen() {
        Intent(requireContext(), LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }
}
