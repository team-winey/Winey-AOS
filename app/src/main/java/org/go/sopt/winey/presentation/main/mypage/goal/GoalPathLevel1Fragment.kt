package org.go.sopt.winey.presentation.main.mypage.goal

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentGoalPathLevel1Binding
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import javax.inject.Inject

@AndroidEntryPoint
class GoalPathLevel1Fragment :
    BindingFragment<FragmentGoalPathLevel1Binding>(R.layout.fragment_goal_path_level1) {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGoalPathUnlockGuide()
        initAnimatorListener()
    }

    private fun initGoalPathUnlockGuide() {

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
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}
