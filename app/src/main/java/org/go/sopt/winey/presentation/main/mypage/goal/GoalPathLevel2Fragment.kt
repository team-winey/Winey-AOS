package org.go.sopt.winey.presentation.main.mypage.goal

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentGoalPathLevel2Binding
import org.go.sopt.winey.util.binding.BindingFragment

class GoalPathLevel2Fragment: BindingFragment<FragmentGoalPathLevel2Binding>(R.layout.fragment_goal_path_level2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAnimatorListener()
    }

    private fun initAnimatorListener() {
        binding.lottieGoalPathStep2.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                binding.ivGoalPathLv2.isVisible = false
                binding.lottieGoalPathStep2.isVisible = true
            }

            override fun onAnimationEnd(animation: Animator) {
                binding.lottieGoalPathStep2.isVisible = false
                binding.ivGoalPathLv3.isVisible = true
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}
