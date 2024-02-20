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
                    // 귀족 레벨 이미지, 말풍선 제거
                    ivGoalPathLv3.isVisible = false
                    llGoalPathLv4Lock.isVisible = false
                    ivGoalPathLv4LockDashLine.isVisible = false

                    // 로티 애니메이션 뷰 표시
                    lottieGoalPathStep3.isVisible = true
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                with(binding) {
                    // 로티 애니메이션 뷰 제거
                    lottieGoalPathStep3.isVisible = false

                    // 말풍선 제거
                    llGoalPathLv4Lock.isVisible = false
                    ivGoalPathLv4LockDashLine.isVisible = false

                    // 황제 레벨 이미지 표시
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
