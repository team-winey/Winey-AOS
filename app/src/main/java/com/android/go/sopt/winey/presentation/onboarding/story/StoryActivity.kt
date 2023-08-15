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
                        updateNavigationText(FIRST_PAGE_NUM, R.string.story_first_detail_text)
                    }

                    is ThirdStoryFragment -> {
                        supportFragmentManager.popBackStack()
                        updateNavigationText(SECOND_PAGE_NUM, R.string.story_second_detail_text)
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
                    updateNavigationText(SECOND_PAGE_NUM, R.string.story_second_detail_text)
                    navigateAndBackStack<SecondStoryFragment>()
                }

                is SecondStoryFragment -> {
                    updateNavigationText(THIRD_PAGE_NUM, R.string.story_third_detail_text)
                    navigateAndBackStack<ThirdStoryFragment>()
                }

                is ThirdStoryFragment -> navigateToNicknameScreen()
            }
        }
    }

    private fun updateNavigationText(pageNumber: Int, @StringRes resId: Int) {
        viewModel.apply {
            updatePageNumber(pageNumber)
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
        private const val SECOND_PAGE_NUM = 2
        private const val THIRD_PAGE_NUM = 3
    }
}
