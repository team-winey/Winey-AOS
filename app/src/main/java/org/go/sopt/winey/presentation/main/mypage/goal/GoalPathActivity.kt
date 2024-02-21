package org.go.sopt.winey.presentation.main.mypage.goal

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityGoalPathBinding
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.snackBar
import org.go.sopt.winey.util.number.formatAmountNumber
import org.go.sopt.winey.util.view.UiState
import java.text.DecimalFormat

@AndroidEntryPoint
class GoalPathActivity : BindingActivity<ActivityGoalPathBinding>(R.layout.activity_goal_path) {
    private val viewModel by viewModels<GoalPathViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDefaultFragment(savedInstanceState)
        initGetGoalStateObserver()
    }

    private fun setupDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            navigateTo<GoalPathLevel1Fragment>()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_goal_path, T::class.simpleName)
        }
    }

    private fun initGetGoalStateObserver() {
        viewModel.getGoalState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    state.data?.let { goal ->
                        binding.tvGoalPathRemainingMoney.text =
                            getString(R.string.goal_path_remaining_money, goal.remainingMoney.formatAmountNumber())
                        binding.tvGoalPathRemainingFeed.text =
                            getString(R.string.goal_path_remaining_feed, goal.remainingFeed)
                    }
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> {}
            }
        }.launchIn(lifecycleScope)
    }
}
