package org.go.sopt.winey.presentation.main.mypage.goal

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentGoalPathLevel3Binding
import org.go.sopt.winey.util.binding.BindingFragment

class GoalPathLevel3Fragment :
    BindingFragment<FragmentGoalPathLevel3Binding>(R.layout.fragment_goal_path_level3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAnimatorListener()
    }

    private fun initAnimatorListener() {
        binding.lottieGoalPathStep3.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                with(binding) {
                    ivGoalPathLv3.isVisible = false
                    lottieGoalPathStep3.isVisible = true
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                with(binding) {
                    lottieGoalPathStep3.isVisible = false
                    ivGoalPathLv4.isVisible = true
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}
