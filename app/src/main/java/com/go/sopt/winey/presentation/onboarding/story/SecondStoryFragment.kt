package com.go.sopt.winey.presentation.onboarding.story

import android.os.Bundle
import android.view.View
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.FragmentSecondStoryBinding
import com.go.sopt.winey.util.binding.BindingFragment

class SecondStoryFragment :
    BindingFragment<FragmentSecondStoryBinding>(R.layout.fragment_second_story) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bringTextToFront()
    }

    private fun bringTextToFront() {
        with(binding) {
            tvStorySaverSpeechBubble.bringToFront()
            tvStoryWineyCountryName.bringToFront()
            tvStoryWineyCountrySpeechBubble.bringToFront()
        }
    }
}