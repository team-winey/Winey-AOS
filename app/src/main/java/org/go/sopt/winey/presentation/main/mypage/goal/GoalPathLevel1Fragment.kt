package org.go.sopt.winey.presentation.main.mypage.goal

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.flowWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentGoalPathLevel1Binding
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.drawableOf
import org.go.sopt.winey.util.fragment.viewLifeCycle
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import javax.inject.Inject

@AndroidEntryPoint
class GoalPathLevel1Fragment :
    BindingFragment<FragmentGoalPathLevel1Binding>(R.layout.fragment_goal_path_level1) {
    private val viewModel by activityViewModels<GoalPathViewModel>()

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGoalPathUnlockGuide()
        initLevelUpStateObserver()
        initAnimatorListener()
    }

    private fun initGoalPathUnlockGuide() {
        viewLifeCycleScope.launch {
            val user = dataStoreRepository.getUserInfo().firstOrNull() ?: return@launch
            if (user.remainingAmount > 0 && user.remainingCount > 0) {
                // 어떤 것도 달성하지 못한 경우
                binding.ivGoalPathLv1.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv1_1))
            } else if (user.remainingAmount == 0 && user.remainingCount > 0) {
                // 누적 금액 달성한 경우
                binding.ivGoalPathLv1.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv1_2))
            } else if (user.remainingAmount > 0 && user.remainingCount == 0) {
                // 누적 횟수 달성한 경우
                binding.ivGoalPathLv1.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv1_3))
            }
        }
    }

    private fun initLevelUpStateObserver() {
        viewModel.levelUpState.flowWithLifecycle(viewLifeCycle).onEach { nowLevelUp ->
            if (nowLevelUp) {
                binding.ivGoalPathLv1.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv1_4))
                binding.lottieGoalPathStep1.playAnimation()
            }
        }.launchIn(viewLifeCycleScope)
    }

    private fun initAnimatorListener() {
        binding.lottieGoalPathStep1.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                with(binding) {
                    ivGoalPathLv1.isVisible = false
                    lottieGoalPathStep1.isVisible = true
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                with(binding) {
                    lottieGoalPathStep1.isVisible = false
                    ivGoalPathLv2.isVisible = true
                }
                navigateTo<GoalPathLevel2Fragment>()
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
