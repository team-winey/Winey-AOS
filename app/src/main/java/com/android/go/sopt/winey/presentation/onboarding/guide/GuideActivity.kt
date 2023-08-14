package com.android.go.sopt.winey.presentation.onboarding.guide

import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityGuideBinding
import com.android.go.sopt.winey.presentation.onboarding.login.LoginActivity
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.colorOf
import com.android.go.sopt.winey.util.context.stringOf

class GuideActivity : BindingActivity<ActivityGuideBinding>(R.layout.activity_guide) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateStatusBarColor(R.color.gray_50)
        setUpDefaultFragment(savedInstanceState)
        initNextButtonClickListener()
    }

    private fun initNextButtonClickListener() {
        binding.btnGuideNext.setOnClickListener {
            when (supportFragmentManager.findFragmentById(R.id.fcv_guide)) {
                is WineyFeedGuideFragment -> navigateTo<RecommendGuideFragment>()

                is RecommendGuideFragment -> {
                    updateStatusBarColor(R.color.white)
                    updateNextButtonText(R.string.guide_start_btn_text)
                    navigateTo<LevelGuideFragment>()
                }

                is LevelGuideFragment -> navigateToLoginScreen()
            }
        }
    }

    private fun updateStatusBarColor(@ColorRes resId: Int) {
        window.statusBarColor = colorOf(resId)
    }

    private fun updateNextButtonText(@StringRes resId: Int) {
        binding.btnGuideNext.text = stringOf(resId)
    }

    private fun setUpDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            navigateTo<WineyFeedGuideFragment>()
        }
    }

    private fun navigateToLoginScreen() {
        Intent(this@GuideActivity, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_guide, T::class.simpleName)
        }
    }
}
