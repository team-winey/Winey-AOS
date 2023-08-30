package com.go.sopt.winey.presentation.onboarding.guide

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class GuideFragmentStateAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments =
        listOf(WineyFeedGuideFragment(), RecommendGuideFragment(), LevelGuideFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
