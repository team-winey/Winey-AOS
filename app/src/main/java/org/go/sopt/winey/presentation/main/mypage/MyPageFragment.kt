package org.go.sopt.winey.presentation.main.mypage

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentMyPageBinding
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.presentation.main.MainViewModel
import org.go.sopt.winey.presentation.main.mypage.goal.GoalPathActivity
import org.go.sopt.winey.presentation.main.mypage.myfeed.MyFeedActivity
import org.go.sopt.winey.presentation.main.mypage.setting.SettingActivity
import org.go.sopt.winey.presentation.main.notification.NotificationActivity
import org.go.sopt.winey.presentation.model.SavePeriod
import org.go.sopt.winey.presentation.model.UserLevel
import org.go.sopt.winey.presentation.model.WineyFeedType
import org.go.sopt.winey.presentation.nickname.NicknameActivity
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.drawableOf
import org.go.sopt.winey.util.fragment.snackBar
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import org.go.sopt.winey.util.number.formatAmountNumber
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.setOnSingleClickListener
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val isFromNoti by lazy { arguments?.getBoolean(MainActivity.KEY_FROM_NOTI, false) }

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amplitudeUtils.logEvent("view_mypage")

        initUserData()
        addListener()
        addObserver()
        registerBackPressedCallback()
    }

    // 닉네임 액티비티 갔다가 다시 돌아왔을 때 유저 데이터 갱신하도록
    override fun onStart() {
        super.onStart()
        mainViewModel.getUser()
    }

    private fun initUserData() {
        viewLifeCycleScope.launch {
            val userInfo = dataStoreRepository.getUserInfo().firstOrNull() ?: return@launch
            updateUserInfo(userInfo)
        }
    }

    private fun addListener() {
        initEditNicknameButtonClickListener()
        initSettingButtonClickListener()
        initMyFeedButtonClickListener()
        initGoalPathButtonClickListener()
    }

    private fun addObserver() {
        setupGetUserStateObserver()
    }

    private fun initEditNicknameButtonClickListener() {
        binding.ivMypageEditNickname.setOnSingleClickListener {
            navigateToNicknameScreen()
        }
    }

    private fun initSettingButtonClickListener() {
        binding.ivMypageSetting.setOnClickListener {
            navigateToSettingScreen()
        }
    }

    private fun initMyFeedButtonClickListener() {
        binding.btnMypageMyfeed.setOnSingleClickListener {
            navigateToMyFeedScreen()
        }
    }

    private fun initGoalPathButtonClickListener() {
        binding.btnMypageTrip.setOnClickListener {
            navigateToGoalPathScreen()
        }
    }

    // 마이페이지 왔다가 다시 알림 화면으로 돌아가도록
    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFromNoti == true) {
                    Intent(requireContext(), NotificationActivity::class.java).apply {
                        startActivity(this)
                    }
                } else {
                    activity?.finish()
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    private fun navigateToNicknameScreen() {
        Intent(requireContext(), NicknameActivity::class.java).apply {
            putExtra(KEY_PREV_SCREEN_NAME, MY_PAGE_SCREEN)
            startActivity(this)
        }
    }

    private fun navigateToSettingScreen() {
        Intent(requireContext(), SettingActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun navigateToMyFeedScreen() {
        Intent(requireContext(), MyFeedActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun navigateToGoalPathScreen() {
        Intent(requireContext(), GoalPathActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun calculateSaveProgressBar(accumulatedAmount: Int, goalAmount: Int): Int {
        return when {
            accumulatedAmount <= 0 -> 0
            accumulatedAmount <= goalAmount -> ((accumulatedAmount / goalAmount.toDouble()) * 100).toInt()
            else -> 100
        }
    }

    private fun setUpUserGoalByLevel(data: UserV2) {
        binding.apply {
            when (data.userLevel) {
                UserLevel.FIRST.rankName -> {
                    tvMypageProfileGoalItem.text = getString(R.string.mypage_goal_lv1)
                    tvMypageGoalCount.text = getString(R.string.mypage_goal_count_lv1)
                    tvMypageProfileGoal.text = getString(R.string.mypage_goal_money_lv1)
                    pbMypage.progress = calculateSaveProgressBar(data.accumulatedAmount, 30000)
                }

                UserLevel.SECOND.rankName -> {
                    tvMypageProfileGoalItem.text = getString(R.string.mypage_goal_lv2)
                    tvMypageGoalCount.text = getString(R.string.mypage_goal_count_lv2)
                    tvMypageProfileGoal.text = getString(R.string.mypage_goal_money_lv2)
                    pbMypage.progress = calculateSaveProgressBar(data.accumulatedAmount, 150000)
                }

                UserLevel.THIRD.rankName -> {
                    tvMypageProfileGoalItem.text = getString(R.string.mypage_goal_lv3)
                    tvMypageGoalCount.text = getString(R.string.mypage_goal_count_lv3)
                    tvMypageProfileGoal.text = getString(R.string.mypage_goal_money_lv3)
                    pbMypage.progress = calculateSaveProgressBar(data.accumulatedAmount, 300000)
                }

                UserLevel.FOURTH.rankName -> {
                    tvMypageProfileGoalItem.text = getString(R.string.mypage_goal_lv4)
                    tvMypageGoalCount.text = getString(R.string.mypage_goal_count_lv3)
                    tvMypageProfileGoal.text = getString(R.string.mypage_goal_money_lv4)
                    tvMypageProfileCurrent.isVisible = false
                    pbMypage.progress = 100
                }
            }
        }
    }

    private fun setUpUserDataByGoal(data: UserV2) {
        binding.apply {
            tvMypageProfileMoney.text =
                if (data.userLevel != UserLevel.FOURTH.rankName) {
                    getString(
                        R.string.mypage_reamining_amount,
                        (data.remainingAmount).formatAmountNumber()
                    )
                } else {
                    getString(R.string.mypage_lv4)
                }
            tvMypageProfileCurrent.text = getString(
                R.string.mypage_current_amount,
                (data.accumulatedAmount).formatAmountNumber()
            )

            if (data.isLevelUpAmountConditionMet) {
                ivMypageGoalMoney.setImageDrawable(drawableOf(R.drawable.ic_mypage_checked))
            } else {
                ivMypageGoalMoney.setImageDrawable(drawableOf(R.drawable.ic_mypage_unchecked))
            }

            if (data.isLevelUpCountConditionMet) {
                tvMypageGoalCountCurrent.text =
                    getString(R.string.mypage_goal_count_complete, data.accumulatedCount)
                ivMypageGoalCount.setImageDrawable(drawableOf(R.drawable.ic_mypage_checked))
            } else {
                tvMypageGoalCountCurrent.text =
                    getString(R.string.mypage_goal_count_incomplete, data.accumulatedCount)
                ivMypageGoalCount.setImageDrawable(drawableOf(R.drawable.ic_mypage_unchecked))
            }

            setMyPageWorkHoursAndType(
                binding.tvMypageSaveWork,
                data.amountSavedTwoWeeks,
                WineyFeedType.SAVE,
                TYPE_WORK
            )
            setMyPageWorkHoursAndType(
                binding.tvMypageSave1Year,
                data.amountSavedTwoWeeks,
                WineyFeedType.SAVE,
                SavePeriod.ONE_YEAR.period
            )
            setMyPageWorkHoursAndType(
                binding.tvMypageSpendWork,
                data.amountSpentTwoWeeks,
                WineyFeedType.CONSUME,
                TYPE_WORK
            )
            setMyPageWorkHoursAndType(
                binding.tvMypageSpend1Year,
                data.amountSpentTwoWeeks,
                WineyFeedType.CONSUME,
                SavePeriod.ONE_YEAR.period
            )
        }
    }

    private fun setupGetUserStateObserver() {
        mainViewModel.getUserState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val data = dataStoreRepository.getUserInfo().first() ?: return@onEach
                    updateUserInfo(data)
                    setUpUserGoalByLevel(data)
                    setUpUserDataByGoal(data)
                    animate2weeksSaveGraph(data.amountSavedTwoWeeks)
                    animate2weeksSpendGraph(data.amountSpentTwoWeeks)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> {}
            }
        }.launchIn(lifecycleScope)
    }

    private fun updateUserInfo(data: UserV2) {
        binding.data = data
    }

    private fun animate2weeksSaveGraph(amountSavedTwoWeeks: Int) {
        animateTextView(
            textView = binding.vMypage2weeks1Month,
            amount = amountSavedTwoWeeks * VALUE_FOR_1_MONTH,
            periodType = SavePeriod.ONE_MONTH.period,
            moneyType = WineyFeedType.SAVE
        )
        animateTextView(
            textView = binding.vMypage2weeks3Month,
            amount = amountSavedTwoWeeks * VALUE_FOR_3_MONTH,
            periodType = SavePeriod.THREE_MONTH.period,
            moneyType = WineyFeedType.SAVE
        )
        animateTextView(
            textView = binding.vMypage2weeks1Year,
            amount = amountSavedTwoWeeks * VALUE_FOR_1_YEAR,
            periodType = SavePeriod.ONE_YEAR.period,
            moneyType = WineyFeedType.SAVE
        )
    }

    private fun animate2weeksSpendGraph(amountSpendTwoWeeks: Int) {
        animateTextView(
            textView = binding.vMypageSpend2weeks1Month,
            amount = amountSpendTwoWeeks * VALUE_FOR_1_MONTH,
            periodType = SavePeriod.ONE_MONTH.period,
            moneyType = WineyFeedType.CONSUME
        )
        animateTextView(
            textView = binding.vMypageSpend2weeks3Month,
            amount = amountSpendTwoWeeks * VALUE_FOR_3_MONTH,
            periodType = SavePeriod.THREE_MONTH.period,
            moneyType = WineyFeedType.CONSUME
        )
        animateTextView(
            textView = binding.vMypageSpend2weeks1Year,
            amount = amountSpendTwoWeeks * VALUE_FOR_1_YEAR,
            periodType = SavePeriod.ONE_YEAR.period,
            moneyType = WineyFeedType.CONSUME
        )
    }

    private fun getGraphAnimationWidth(textViewWidth: Int, type: String): Int {
        return when (type) {
            SavePeriod.ONE_MONTH.period -> textViewWidth * 3 / 5
            SavePeriod.THREE_MONTH.period -> textViewWidth * 2 / 3
            SavePeriod.ONE_YEAR.period -> textViewWidth * 5 / 6
            else -> 0
        }
    }

    private fun animateTextView(
        textView: TextView,
        amount: Int,
        periodType: String,
        moneyType: WineyFeedType
    ) {
        val params = textView.layoutParams
        val parentView = textView.parent as ViewGroup
        val textViewWidth = parentView.measuredWidth - binding.tvMypage2weeks1Year.width
        val width = getGraphAnimationWidth(textViewWidth, periodType)
        textView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val animator = ValueAnimator.ofInt(0, width).apply {
                        duration = ANIMATION_DURATION.toLong()
                        addUpdateListener { valueAnimator ->
                            params?.width = valueAnimator.animatedValue as Int
                            textView.requestLayout()
                        }
                        doOnStart {
                            textView.text = ""
                        }
                        doOnEnd {
                            textView.text = when (moneyType) {
                                WineyFeedType.SAVE -> {
                                    String.format(
                                        getString(R.string.mypage_save_money),
                                        amount.formatAmountNumber()
                                    )
                                }

                                WineyFeedType.CONSUME -> {
                                    String.format(
                                        getString(R.string.mypage_spend_money),
                                        amount.formatAmountNumber()
                                    )
                                }
                            }
                        }
                    }
                    animator.start()
                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    startAnimationOnVisible(binding.svMypage, textView, animator)
                }
            })
    }

    fun startAnimationOnVisible(
        scrollView: ScrollView,
        textView: TextView,
        animator: ValueAnimator
    ) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = scrollView.scrollY
            val location = IntArray(2)
            textView.getLocationOnScreen(location)
            if (location[1] > scrollY && location[1] + textView.height < scrollY + scrollView.height) {
                animator.start()
            }
        }
    }

    private fun setMyPageWorkHoursAndType(
        textView: TextView,
        money: Int,
        moneyType: WineyFeedType,
        textType: String
    ) {
        var amountText = ""
        var fullText = ""
        when (textType) {
            TYPE_WORK -> {
                amountText = (money / HOURLY_PAY).toString() + "시간"
                fullText = getString(R.string.mypage_2weeks_save_for_job, amountText)
            }

            SavePeriod.ONE_YEAR.period -> {
                amountText = (money * VALUE_FOR_1_YEAR).formatAmountNumber() + "원"
                fullText = when (moneyType) {
                    WineyFeedType.SAVE -> {
                        getString(R.string.mypage_2weeks_save_for_1year, amountText)
                    }

                    WineyFeedType.CONSUME -> {
                        getString(R.string.mypage_2weeks_spend_for_1year, amountText)
                    }
                }
            }
        }
        val startIndex = fullText.indexOf(amountText)
        val endIndex = startIndex + amountText.length

        val spannableString = SpannableString(fullText).apply {
            when (moneyType) {
                WineyFeedType.SAVE -> {
                    setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.sub_green_500
                            )
                        ),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                WineyFeedType.CONSUME -> {
                    setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.sub_red_500
                            )
                        ),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
        textView.text = spannableString
    }

    companion object {
        @JvmStatic
        fun newInstance(args: Bundle? = null) = MyPageFragment().apply {
            args?.let {
                arguments = it
            }
        }

        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"
        private const val MY_PAGE_SCREEN = "MyPageFragment"
        private const val KEY_FROM_NOTI = "fromNoti"

        private const val HOURLY_PAY = 9860
        private const val VALUE_FOR_1_MONTH = 2
        private const val VALUE_FOR_3_MONTH = 6
        private const val VALUE_FOR_1_YEAR = 24

        private const val ANIMATION_DURATION = 1000
        private const val TYPE_WORK = "WORK"
    }
}
