package org.go.sopt.winey.presentation.main.mypage.goal

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityGoalPathBinding
import org.go.sopt.winey.util.binding.BindingActivity

class GoalPathActivity : BindingActivity<ActivityGoalPathBinding>(R.layout.activity_goal_path) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAnimatorListener()
    }

    private fun initAnimatorListener() {
        binding.lottieGoalPathLevelUp1.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                binding.ivGoalPathLv1.isVisible = false
                binding.lottieGoalPathLevelUp1.isVisible = true
            }

            override fun onAnimationEnd(animation: Animator) {
                binding.lottieGoalPathLevelUp1.isVisible = false
                binding.ivGoalPathLv2.isVisible = true
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}
