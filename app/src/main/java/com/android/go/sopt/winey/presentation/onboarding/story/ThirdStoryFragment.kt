package com.android.go.sopt.winey.presentation.onboarding.story

import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentThirdStoryBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class ThirdStoryFragment : BindingFragment<FragmentThirdStoryBinding>(R.layout.fragment_third_story) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvStoryWineyCountrySpeechBubble.bringToFront()
    }
}
