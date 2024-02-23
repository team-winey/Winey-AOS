package org.go.sopt.winey.presentation.main.mypage.goal

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityGoalPathBinding
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.feed.WineyFeedFragment
import org.go.sopt.winey.presentation.model.UserLevel
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.number.formatAmountNumber
import javax.inject.Inject

@AndroidEntryPoint
class GoalPathActivity : BindingActivity<ActivityGoalPathBinding>(R.layout.activity_goal_path) {
    private val viewModel by viewModels<GoalPathViewModel>()
    private val nowLevelUp by lazy { intent.getBooleanExtra(WineyFeedFragment.KEY_LEVEL_UP, false) }

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initLevelUpState(nowLevelUp)
        setupFragmentByLevel()
        initRemainingGoal()
    }

    private fun setupFragmentByLevel() {
        lifecycleScope.launch {
            val userInfo = dataStoreRepository.getUserInfo().firstOrNull() ?: return@launch

            when (userInfo.userLevel) {
                UserLevel.FIRST.rankName -> {
                    navigateTo<GoalPathLevel1Fragment>()
                }

                UserLevel.SECOND.rankName -> {
                    if (nowLevelUp) {
                        navigateTo<GoalPathLevel1Fragment>()
                    } else {
                        navigateTo<GoalPathLevel2Fragment>()
                    }
                }

                UserLevel.THIRD.rankName -> {
                    if (nowLevelUp) {
                        navigateTo<GoalPathLevel2Fragment>()
                    } else {
                        navigateTo<GoalPathLevel3Fragment>()
                    }
                }

                UserLevel.FORTH.rankName -> {
                    binding.clGoalPathBackground.setBackgroundResource(R.drawable.img_goal_path_background_lv4)
                    binding.clGoalPathGuide.isVisible = false

                    if (nowLevelUp) {
                        navigateTo<GoalPathLevel3Fragment>()
                    } else {
                        navigateTo<GoalPathLevel4Fragment>()
                    }
                }
            }
        }
    }

    private fun initRemainingGoal() {
        lifecycleScope.launch {
            val userInfo = dataStoreRepository.getUserInfo().firstOrNull() ?: return@launch
            binding.tvGoalPathRemainingMoney.text =
                getString(
                    R.string.goal_path_remaining_money,
                    userInfo.remainingAmount.formatAmountNumber()
                )
            binding.tvGoalPathRemainingFeed.text =
                getString(R.string.goal_path_remaining_feed, userInfo.remainingCount)
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_goal_path, T::class.simpleName)
        }
    }
}
