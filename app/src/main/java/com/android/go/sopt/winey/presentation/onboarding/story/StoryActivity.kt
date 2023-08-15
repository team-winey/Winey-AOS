package com.android.go.sopt.winey.presentation.onboarding.story

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityStoryBinding
import com.android.go.sopt.winey.presentation.onboarding.nickname.NicknameActivity
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.colorOf
import com.android.go.sopt.winey.util.context.stringOf

class StoryActivity : BindingActivity<ActivityStoryBinding>(R.layout.activity_story) {
    private val viewModel by viewModels<StoryViewModel>()
    private var currentPageNumber = FIRST_PAGE_NUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel

        updateStatusBarColor()
        setUpDefaultNavigationText()
        setUpDefaultFragment(savedInstanceState)
        initNextButtonClickListener()
        registerBackPressedCallback()
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (supportFragmentManager.findFragmentById(R.id.fcv_story)) {
                    is FirstStoryFragment -> finish()
                    is SecondStoryFragment -> {
                        supportFragmentManager.popBackStack()
                        updateNavigationText(BACK_DIRECTION, R.string.story_first_detail_text)
                    }

                    is ThirdStoryFragment -> {
                        supportFragmentManager.popBackStack()
                        updateNavigationText(BACK_DIRECTION, R.string.story_second_detail_text)
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initNextButtonClickListener() {
        binding.fabStoryNext.setOnClickListener {
            when (supportFragmentManager.findFragmentById(R.id.fcv_story)) {
                is FirstStoryFragment -> {
                    updateNavigationText(FRONT_DIRECTION, R.string.story_second_detail_text)
                    navigateAndBackStack<SecondStoryFragment>()
                }

                is SecondStoryFragment -> {
                    updateNavigationText(FRONT_DIRECTION, R.string.story_third_detail_text)
                    navigateAndBackStack<ThirdStoryFragment>()
                }

                is ThirdStoryFragment -> navigateToNicknameScreen()
            }
        }
    }

    private fun updateNavigationText(direction: Int, @StringRes resId: Int) {
        viewModel.apply {
            if (direction == BACK_DIRECTION) {
                updatePageNumber(--currentPageNumber)
            } else {
                updatePageNumber(++currentPageNumber)
            }
            updateDetailText(stringOf(resId))
        }
    }

    private fun updateStatusBarColor() {
        window.statusBarColor = colorOf(R.color.gray_50)
    }

    private fun setUpDefaultNavigationText() {
        viewModel.updateDetailText(stringOf(R.string.story_first_detail_text))
    }

    private fun setUpDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fcv_story, FirstStoryFragment())
            }
        }
    }

    private inline fun <reified T : Fragment> navigateAndBackStack() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_story, T::class.simpleName)
            addToBackStack(null)
        }
    }

    private fun navigateToNicknameScreen() {
        Intent(this, NicknameActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    companion object {
        private const val FIRST_PAGE_NUM = 1
        private const val BACK_DIRECTION = -1
        private const val FRONT_DIRECTION = 1
    }
}
