package com.android.go.sopt.winey.presentation.onboarding.story

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentSecondStoryBinding
import com.android.go.sopt.winey.util.binding.BindingFragment
import com.android.go.sopt.winey.util.context.stringOf
import timber.log.Timber

class SecondStoryFragment :
    BindingFragment<FragmentSecondStoryBinding>(R.layout.fragment_second_story) {
    private val viewModel by activityViewModels<StoryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bringSpeechBubbleToFront()
        registerBackPressedCallback()
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.e("BACK BUTTON CLICK IN SECOND")

                viewModel.apply {
                    updatePageNumber(FIRST_PAGE_NUM)
                    updateDetailText(requireContext().stringOf(R.string.story_nav_first_detail))
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun bringSpeechBubbleToFront() {
        binding.tvStoryWineyCountrySpeechBubble.bringToFront()
    }

    companion object {
        private const val FIRST_PAGE_NUM = 1
    }
}
