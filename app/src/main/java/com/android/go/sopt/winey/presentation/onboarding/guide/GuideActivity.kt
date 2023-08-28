package com.android.go.sopt.winey.presentation.onboarding.guide

import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityGuideBinding
import com.android.go.sopt.winey.presentation.onboarding.login.LoginActivity
import com.android.go.sopt.winey.util.amplitude.AmplitudeUtils
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.stringOf
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class GuideActivity : BindingActivity<ActivityGuideBinding>(R.layout.activity_guide) {
    private var currentPosition = 0

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        amplitudeUtils.logEvent("view_onboarding")

        initViewPagerAdapter()
        observePageChange()
        initNextButtonClickListener()
    }

    private fun initViewPagerAdapter() {
        binding.vpGuide.adapter = GuideFragmentStateAdapter(this)
    }

    private fun observePageChange() {
        binding.vpGuide.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position

                when (position) {
                    LAST_PAGE_INDEX ->
                        binding.btnGuideNext.text =
                            stringOf(R.string.guide_start_btn_text)

                    else -> binding.btnGuideNext.text = stringOf(R.string.guide_next_btn_text)
                }
            }
        })
    }

    private fun initNextButtonClickListener() {
        binding.btnGuideNext.setOnClickListener {
            if (currentPosition == LAST_PAGE_INDEX) {
                navigateToLoginScreen()
                return@setOnClickListener
            }

            sendEventToAmplitude()
            binding.vpGuide.setCurrentItem(++currentPosition, true)
        }
    }

    private fun sendEventToAmplitude() {
        val eventProperties = JSONObject()
        try {
            eventProperties.put("button_name", "onboarding_next_button")
                .put("page_number", currentPosition + 1)
        } catch (e: JSONException) {
            System.err.println("Invalid JSON")
            e.printStackTrace()
        }
        amplitudeUtils.logEvent("click_button", eventProperties)
    }

    private fun navigateToLoginScreen() {
        Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    companion object {
        private const val LAST_PAGE_INDEX = 2
    }
}
