package org.go.sopt.winey.presentation.main.mypage.goal

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentGoalPathLevel2Binding
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.model.UserLevel
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.drawableOf
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import javax.inject.Inject

@AndroidEntryPoint
class GoalPathLevel2Fragment :
    BindingFragment<FragmentGoalPathLevel2Binding>(R.layout.fragment_goal_path_level2) {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGoalPathUnlockGuide()
        checkNowLevelUp()
        initAnimatorListener()
    }

    private fun initGoalPathUnlockGuide() {
        viewLifeCycleScope.launch {
            val user = dataStoreRepository.getUserInfo().firstOrNull() ?: return@launch
            if (user.remainingAmount > 0 && user.remainingCount > 0) {
                // 어떤 것도 달성하지 못한 경우
                binding.ivGoalPathLv2.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv2_1))
            } else if (user.remainingAmount == 0 && user.remainingCount > 0) {
                // 누적 금액 달성한 경우
                binding.ivGoalPathLv2.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv2_2))
            } else if (user.remainingAmount > 0 && user.remainingCount == 0) {
                // 누적 횟수 달성한 경우
                binding.ivGoalPathLv2.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv2_3))
            }
        }
    }

    private fun checkNowLevelUp() {
        viewLifeCycleScope.launch {
            val user = dataStoreRepository.getUserInfo().firstOrNull() ?: return@launch
            if (user.userLevel != UserLevel.SECOND.rankName) {
                binding.ivGoalPathLv2.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv2_4))
                binding.lottieGoalPathStep2.playAnimation()
            }
        }
    }

    private fun initAnimatorListener() {
        binding.lottieGoalPathStep2.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                with(binding) {
                    ivGoalPathLv2.isVisible = false
                    lottieGoalPathStep2.isVisible = true
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                with(binding) {
                    lottieGoalPathStep2.isVisible = false
                    ivGoalPathLv3.isVisible = true
                }
                navigateTo<GoalPathLevel3Fragment>()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }

    private inline fun <reified T : Fragment> navigateTo() {
        parentFragmentManager.commit {
            replace<T>(R.id.fcv_goal_path, T::class.simpleName)
        }
    }
}
