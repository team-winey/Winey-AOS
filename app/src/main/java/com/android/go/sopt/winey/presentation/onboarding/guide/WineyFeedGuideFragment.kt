package com.android.go.sopt.winey.presentation.onboarding.guide

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.FragmentWineyFeedGuideBinding
import com.android.go.sopt.winey.util.binding.BindingFragment

class WineyFeedGuideFragment : BindingFragment<FragmentWineyFeedGuideBinding>(R.layout.fragment_winey_feed_guide) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNextButtonClickListener()
    }

    private fun initNextButtonClickListener() {
        binding.btnGuideNext.setOnClickListener {
            navigateTo<RecommendGuideFragment>()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        parentFragmentManager.commit {
            replace<T>(R.id.fcv_guide, T::class.simpleName)
        }
    }
}
