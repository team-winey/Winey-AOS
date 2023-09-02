package org.go.sopt.winey.presentation.onboarding.story

import android.os.Bundle
import android.view.View
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentSecondStoryBinding
import org.go.sopt.winey.util.binding.BindingFragment

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
