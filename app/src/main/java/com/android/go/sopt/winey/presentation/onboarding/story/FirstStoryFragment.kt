package com.android.go.sopt.winey.presentation.onboarding.story

import android.os.Bundle
import android.view.View
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentFirstStoryBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class FirstStoryFragment :
    BindingFragment<FragmentFirstStoryBinding>(R.layout.fragment_first_story) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bringSpeechBubbleToFront()
    }

    private fun bringSpeechBubbleToFront() {
        binding.tvStoryWineyCountrySpeechBubble.bringToFront()
    }
}
