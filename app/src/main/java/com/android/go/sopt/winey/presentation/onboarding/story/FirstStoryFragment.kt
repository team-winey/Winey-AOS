package com.android.go.sopt.winey.presentation.onboarding.story

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentFirstStoryBinding
import com.android.go.sopt.winey.util.binding.BindingFragment
import timber.log.Timber

class FirstStoryFragment :
    BindingFragment<FragmentFirstStoryBinding>(R.layout.fragment_first_story) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bringSpeechBubbleToFront()
        registerBackPressedCallback()
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.e("BACK BUTTON CLICK IN FIRST")
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun bringSpeechBubbleToFront() {
        binding.tvStoryWineyCountrySpeechBubble.bringToFront()
    }
}
