package org.go.sopt.winey.presentation.main.mypage.goal

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
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
import org.go.sopt.winey.util.context.drawableOf
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

        initGoalPath()
        checkNowLevelUp()
        initAnimatorListener()

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

    private fun initGoalPath() {
        when (userInfo?.userLevel) {
            UserLevel.FIRST.rankName -> {
                initLevel1GoalPath()
            }

            UserLevel.SECOND.rankName -> {
                initLevel2GoalPath()
            }

            UserLevel.THIRD.rankName -> {
                initLevel3GoalPath()
            }

            UserLevel.FOURTH.rankName -> {
                initLevel4GoalPath()
            }
        }
    }

    private fun initLevel1GoalPath() {
        userInfo?.let { user ->
            // 어떤 것도 달성하지 못한 경우
            if (!user.isLevelUpAmountConditionMet && !user.isLevelUpCountConditionMet) {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv1_1))
            }
            // 누적 금액 달성한 경우
            else if (user.isLevelUpAmountConditionMet && !user.isLevelUpCountConditionMet) {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv1_2))
            }
            // 누적 업로드 횟수 달성한 경우
            else if (!user.isLevelUpAmountConditionMet && user.isLevelUpCountConditionMet) {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv1_3))
            }
        }
    }

    private fun initLevel2GoalPath() {
        userInfo?.let { user ->
            if (!user.isLevelUpAmountConditionMet && !user.isLevelUpCountConditionMet) {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv2_1))
            } else if (user.isLevelUpAmountConditionMet && !user.isLevelUpCountConditionMet) {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv2_2))
            } else if (!user.isLevelUpAmountConditionMet && user.isLevelUpCountConditionMet) {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv2_3))
            }
        }
    }

    private fun initLevel3GoalPath() {
        userInfo?.let { user ->
            if (!user.isLevelUpAmountConditionMet && !user.isLevelUpCountConditionMet) {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv3_1))
            } else if (user.isLevelUpAmountConditionMet && !user.isLevelUpCountConditionMet) {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv3_2))
            } else if (!user.isLevelUpAmountConditionMet && user.isLevelUpCountConditionMet) {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv3_3))
            }
        }
    }

    private fun initLevel4GoalPath() {
        with(binding) {
            ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv4))
            clGoalPathBackground.setBackgroundResource(R.drawable.img_goal_path_background_lv4)
            clGoalPathGuideOther.isVisible = false
            clGoalPathGuideLv4.isVisible = true
        }
    }

    private fun checkNowLevelUp() {
        if (levelUpFromWineyFeed) {
            hideGoalPathGuideBox()
            playLottieAnimation()
        }
    }

    private fun hideGoalPathGuideBox() {
        binding.clGoalPathGuideOther.isVisible = false
        binding.clGoalPathGuideLv4.isVisible = false
    }

    private fun playLottieAnimation() {
        when (userInfo?.userLevel) {
            UserLevel.SECOND.rankName -> {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv1_4))
                binding.lottieGoalPath.apply {
                    setAnimation(R.raw.lottie_goal_path_step1)
                    playAnimation()
                }
            }

            UserLevel.THIRD.rankName -> {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv2_4))
                binding.lottieGoalPath.apply {
                    setAnimation(R.raw.lottie_goal_path_step2)
                    playAnimation()
                }
            }

            UserLevel.FOURTH.rankName -> {
                binding.ivGoalPathBefore.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv3_4))
                binding.lottieGoalPath.apply {
                    setAnimation(R.raw.lottie_goal_path_step3)
                    playAnimation()
                }
            }
        }
    }

    private fun initAnimatorListener() {
        binding.lottieGoalPath.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                with(binding) {
                    ivGoalPathBefore.isVisible = false
                    lottieGoalPath.isVisible = true
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                with(binding) {
                    lottieGoalPath.isVisible = false
                    ivGoalPathAfter.isVisible = true
                    updateGoalPath()
                    updateGoalPathGuideBox()
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }

    private fun updateGoalPath() {
        when (userInfo?.userLevel) {
            UserLevel.SECOND.rankName -> {
                binding.ivGoalPathAfter.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv2_1))
            }

            UserLevel.THIRD.rankName -> {
                binding.ivGoalPathAfter.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv3_1))
            }

            UserLevel.FOURTH.rankName -> {
                binding.ivGoalPathAfter.setImageDrawable(drawableOf(R.drawable.img_goal_path_lv4))
            }
        }
    }

    private fun updateGoalPathGuideBox() {
        if (userInfo?.userLevel == UserLevel.FOURTH.rankName) {
            binding.clGoalPathGuideLv4.isVisible = true
            binding.clGoalPathGuideOther.isVisible = false
        } else {
            binding.clGoalPathGuideLv4.isVisible = false
            binding.clGoalPathGuideOther.isVisible = true
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
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.KEY_FROM_GOAL_PATH, true)
            startActivity(this)
        }
    }
}
