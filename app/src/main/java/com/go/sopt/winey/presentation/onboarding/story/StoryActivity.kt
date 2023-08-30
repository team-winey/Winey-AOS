package com.go.sopt.winey.presentation.onboarding.story

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.ActivityStoryBinding
import com.go.sopt.winey.presentation.nickname.NicknameActivity
import com.go.sopt.winey.util.amplitude.AmplitudeUtils
import com.go.sopt.winey.util.binding.BindingActivity
import com.go.sopt.winey.util.context.colorOf
import com.go.sopt.winey.util.context.stringOf
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class StoryActivity : BindingActivity<ActivityStoryBinding>(R.layout.activity_story) {
    private val viewModel by viewModels<StoryViewModel>()
    private var currentPageNumber = FIRST_PAGE_NUM

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        amplitudeUtils.logEvent("view_storytelling")

        updateStatusBarColor()
        setUpDefaultNavigationText()
        setUpDefaultFragment(savedInstanceState)
        initNextButtonClickListener()
        initSkipButtonClickListener()
    }

    private fun initSkipButtonClickListener() {
        binding.tvStorySkip.setOnClickListener {
            navigateToNicknameScreen()
        }
    }

    private fun initNextButtonClickListener() {
        binding.fabStoryNext.setOnClickListener {
            when (supportFragmentManager.findFragmentById(R.id.fcv_story)) {
                is FirstStoryFragment -> {
                    updateNavigationText(R.string.story_second_detail_text)
                    sendEventToAmplitude()
                    navigateTo<SecondStoryFragment>()
                }

                is SecondStoryFragment -> {
                    updateNavigationText(R.string.story_third_detail_text)
                    sendEventToAmplitude()
                    navigateTo<ThirdStoryFragment>()
                }

                is ThirdStoryFragment -> navigateToNicknameScreen()
            }
        }
    }

    private fun updateNavigationText(@StringRes resId: Int) {
        viewModel.apply {
            updatePageNumber(++currentPageNumber)
            updateDetailText(stringOf(resId))
        }
    }

    private fun sendEventToAmplitude() {
        val eventProperties = JSONObject()

        try {
            eventProperties.put("button_name", "storytelling_next_button")
                .put("paging_number", currentPageNumber)
        } catch (e: JSONException) {
            System.err.println("Invalid JSON")
            e.printStackTrace()
        }

        amplitudeUtils.logEvent("click_button", eventProperties)
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

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_story, T::class.simpleName)
        }
    }

    private fun navigateToNicknameScreen() {
        Intent(this, NicknameActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(EXTRA_KEY, EXTRA_VALUE)
            startActivity(this)
        }
    }

    companion object {
        private const val FIRST_PAGE_NUM = 1
        private const val EXTRA_KEY = "PREV_SCREEN_NAME"
        private const val EXTRA_VALUE = "StoryActivity"
    }
}
