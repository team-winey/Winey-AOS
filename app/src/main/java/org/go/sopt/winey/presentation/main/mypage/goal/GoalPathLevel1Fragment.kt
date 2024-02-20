package org.go.sopt.winey.presentation.main.mypage.goal

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentGoalPathLevel1Binding
import org.go.sopt.winey.util.binding.BindingFragment

class GoalPathLevel1Fragment :
    BindingFragment<FragmentGoalPathLevel1Binding>(R.layout.fragment_goal_path_level1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAnimatorListener()
    }

    private fun initAnimatorListener() {
        binding.lottieGoalPathStep1.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                with(binding) {
                    // 평민 레벨 이미지, 말풍선 제거
                    ivGoalPathLv1.isVisible = false
                    llGoalPathLv2Lock.isVisible = false
                    ivGoalPathLv2LockDashLine.isVisible = false

                    // 로티 애니메이션 뷰 표시
                    lottieGoalPathStep1.isVisible = true
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                with(binding) {
                    // 로티 애니메이션 뷰 제거
                    lottieGoalPathStep1.isVisible = false

                    // 말풍선 제거
                    llGoalPathLv2Lock.isVisible = false
                    ivGoalPathLv2LockDashLine.isVisible = false

                    // 기사 레벨 이미지 표시
                    ivGoalPathLv2.isVisible = true
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}
