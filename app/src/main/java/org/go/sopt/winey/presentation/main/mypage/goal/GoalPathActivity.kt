package org.go.sopt.winey.presentation.main.mypage.goal

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
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
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.presentation.main.feed.WineyFeedFragment
import org.go.sopt.winey.presentation.model.UserLevel
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.number.formatAmountNumber
import javax.inject.Inject

@AndroidEntryPoint
class GoalPathActivity : BindingActivity<ActivityGoalPathBinding>(R.layout.activity_goal_path) {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    private var userInfo: UserV2? = null
    private val levelUpFromWineyFeed by lazy {
        intent.getBooleanExtra(
            WineyFeedFragment.KEY_LEVEL_UP,
            false
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUserData()
        initRemainingGoal()
        setupFragmentByLevel()
        initBackButtonClickListener()
        registerBackPressedCallback()
    }

    private fun initUserData() {
        lifecycleScope.launch {
            userInfo = dataStoreRepository.getUserInfo().firstOrNull() ?: return@launch
        }
    }

    private fun initRemainingGoal() {
        lifecycleScope.launch {
            binding.tvGoalPathRemainingMoney.text =
                getString(
                    R.string.goal_path_remaining_money,
                    userInfo?.remainingAmount?.formatAmountNumber()
                )
            binding.tvGoalPathRemainingFeed.text =
                getString(R.string.goal_path_remaining_feed, userInfo?.remainingCount)
        }
    }

    private fun setupFragmentByLevel() {
        lifecycleScope.launch {
            when (userInfo?.userLevel) {
                UserLevel.FIRST.rankName -> {
                    navigateTo<GoalPathLevel1Fragment>()
                }

                UserLevel.SECOND.rankName -> {
                    if (levelUpFromWineyFeed) {
                        navigateTo<GoalPathLevel1Fragment>()
                    } else {
                        navigateTo<GoalPathLevel2Fragment>()
                    }
                }

                UserLevel.THIRD.rankName -> {
                    if (levelUpFromWineyFeed) {
                        navigateTo<GoalPathLevel2Fragment>()
                    } else {
                        navigateTo<GoalPathLevel3Fragment>()
                    }
                }

                UserLevel.FORTH.rankName -> {
                    binding.clGoalPathBackground.setBackgroundResource(R.drawable.img_goal_path_background_lv4)
                    binding.clGoalPathGuide.isVisible = false

                    if (levelUpFromWineyFeed) {
                        navigateTo<GoalPathLevel3Fragment>()
                    } else {
                        navigateTo<GoalPathLevel4Fragment>()
                    }
                }
            }
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivGoalPathBack.setOnClickListener {
            if (levelUpFromWineyFeed) {
                navigateToMainScreen()
            } else {
                finish()
            }
        }
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (levelUpFromWineyFeed) {
                    navigateToMainScreen()
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun navigateToMainScreen() {
        Intent(this@GoalPathActivity, MainActivity::class.java).apply {
            putExtra(MainActivity.KEY_FROM_GOAL_PATH, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_goal_path, T::class.simpleName)
        }
    }
}
