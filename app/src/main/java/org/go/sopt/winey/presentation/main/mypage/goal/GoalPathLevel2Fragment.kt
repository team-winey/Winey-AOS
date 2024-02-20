package org.go.sopt.winey.presentation.main.mypage.goal

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentGoalPathLevel2Binding
import org.go.sopt.winey.util.binding.BindingFragment

class GoalPathLevel2Fragment :
    BindingFragment<FragmentGoalPathLevel2Binding>(R.layout.fragment_goal_path_level2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAnimatorListener()
    }

    private fun initAnimatorListener() {
        binding.lottieGoalPathStep2.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                with(binding) {
                    // 기사 레벨 이미지, 말풍선 제거
                    ivGoalPathLv2.isVisible = false
                    llGoalPathLv3Lock.isVisible = false
                    ivGoalPathLv3LockDashLine.isVisible = false

                    // 로티 애니메이션 뷰 표시
                    lottieGoalPathStep2.isVisible = true
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                with(binding) {
                    // 로티 애니메이션 뷰 제거
                    lottieGoalPathStep2.isVisible = false

                    // 말풍선 제거
                    llGoalPathLv3Lock.isVisible = false
                    ivGoalPathLv3LockDashLine.isVisible = false

                    // 귀족 레벨 이미지 표시
                    ivGoalPathLv3.isVisible = true
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}
