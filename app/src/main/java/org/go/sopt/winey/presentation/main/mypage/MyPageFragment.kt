package org.go.sopt.winey.presentation.main.mypage

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import org.go.sopt.winey.presentation.nickname.NicknameActivity
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.currency.MoneyCurrency.formatWithCommaForMoney
import org.go.sopt.winey.util.fragment.drawableOf
import org.go.sopt.winey.util.fragment.snackBar
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
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

    private fun setUpUserGoalByLevel(data: UserV2) {
        binding.apply {
            when (data.userLevel) {
                "평민" -> {
                    tvMypageProfileGoalItem.text = getString(R.string.mypage_goal_lv1)
                    tvMypageGoalMoney.text = getString(R.string.mypage_goal_amount_lv1)
                    tvMypageGoalCount.text = getString(R.string.mypage_goal_count_lv1)
                    tvMypageProfileGoal.text = "3만원"
                    pbMypage.progress = when {
                        data.accumulatedAmount <= 0 -> 0
                        data.accumulatedAmount <= 30000 -> ((data.accumulatedAmount / 30000.0) * 100).toInt()
                        else -> 100
                    }
                }

                "기사" -> {
                    tvMypageProfileGoalItem.text = getString(R.string.mypage_goal_lv2)
                    tvMypageGoalMoney.text = getString(R.string.mypage_goal_amount_lv2)
                    tvMypageGoalCount.text = getString(R.string.mypage_goal_count_lv2)
                    tvMypageProfileGoal.text = "15만원"
                    pbMypage.progress = when {
                        data.accumulatedAmount <= 0 -> 0
                        data.accumulatedAmount <= 150000 -> ((data.accumulatedAmount / 150000.0) * 100).toInt()
                        else -> 100
                    }
                }

                "귀족" -> {
                    tvMypageProfileGoalItem.text = getString(R.string.mypage_goal_lv3)
                    tvMypageGoalMoney.text = getString(R.string.mypage_goal_amount_lv3)
                    tvMypageGoalCount.text = getString(R.string.mypage_goal_count_lv3)
                    tvMypageProfileGoal.text = "30만원"
                    pbMypage.progress = when {
                        data.accumulatedAmount <= 0 -> 0
                        data.accumulatedAmount <= 300000 -> ((data.accumulatedAmount / 300000.0) * 100).toInt()
                        else -> 100
                    }
                }

                "황제" -> {
                    tvMypageProfileGoalItem.text = getString(R.string.mypage_goal_lv4)
                    tvMypageGoalMoney.text = getString(R.string.mypage_goal_amount_lv3)
                    tvMypageGoalCount.text = getString(R.string.mypage_goal_count_lv3)
                    tvMypageProfileGoal.text = "레벨 미션 완료"
                    tvMypageProfileCurrent.isVisible = false
                    pbMypage.progress = 100
                }
            }
        }
    }

    private fun setUpUserDataByGoal(data: UserV2) {
        binding.apply {
            tvMypageProfileMoney.text =
                if (data.userLevel != "황제") {
                    getString(
                        R.string.mypage_reamining_amount,
                        formatWithCommaForMoney(data.remainingAmount)
                    )
                } else {
                    getString(R.string.mypage_lv4)
                }
            tvMypageProfileCurrent.text = getString(
                R.string.mypage_current_amount,
                formatWithCommaForMoney(data.accumulatedAmount)
            )

            if (data.isLevelUpAmountConditionMet) {
                tvMypageGoalMoneyCurrent.text = getString(
                    R.string.mypage_goal_amount_complete,
                    formatWithCommaForMoney(data.accumulatedAmount)
                )
                ivMypageGoalMoney.setImageDrawable(drawableOf(R.drawable.ic_mypage_checked))
            } else {
                tvMypageGoalMoneyCurrent.text = getString(
                    R.string.mypage_goal_amount_incomplete,
                    formatWithCommaForMoney(data.accumulatedAmount)
                )
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
                "SAVE",
                "WORK"
            )
            setMyPageWorkHoursAndType(
                binding.tvMypageSave1Year,
                data.amountSavedTwoWeeks,
                "SAVE",
                "1YEAR"
            )
            setMyPageWorkHoursAndType(
                binding.tvMypageSpendWork,
                data.amountSpentTwoWeeks,
                "SPEND",
                "WORK"
            )
            setMyPageWorkHoursAndType(
                binding.tvMypageSpend1Year,
                data.amountSpentTwoWeeks,
                "SPEND",
                "1YEAR"
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
            amount = amountSavedTwoWeeks * 2,
            periodType = "1MONTH",
            moneyType = "SAVE"
        )
        animateTextView(
            textView = binding.vMypage2weeks3Month,
            amount = amountSavedTwoWeeks * 6,
            periodType = "3MONTH",
            moneyType = "SAVE"
        )
        animateTextView(
            textView = binding.vMypage2weeks1Year,
            amount = amountSavedTwoWeeks * 24,
            periodType = "1YEAR",
            moneyType = "SAVE"
        )
    }

    private fun animate2weeksSpendGraph(amountSpendTwoWeeks: Int) {
        animateTextView(
            textView = binding.vMypageSpend2weeks1Month,
            amount = amountSpendTwoWeeks * 2,
            periodType = "1MONTH",
            moneyType = "SPEND"
        )
        animateTextView(
            textView = binding.vMypageSpend2weeks3Month,
            amount = amountSpendTwoWeeks * 6,
            periodType = "3MONTH",
            moneyType = "SPEND"
        )
        animateTextView(
            textView = binding.vMypageSpend2weeks1Year,
            amount = amountSpendTwoWeeks * 24,
            periodType = "1YEAR",
            moneyType = "SPEND"
        )
    }

    private fun getGraphAnimationWidth(textViewWidth: Int, type: String): Int {
        return when (type) {
            "1MONTH" -> textViewWidth * 3 / 5
            "3MONTH" -> textViewWidth * 2 / 3
            "1YEAR" -> textViewWidth * 5 / 6
            else -> 0
        }
    }

    private fun animateTextView(
        textView: TextView,
        amount: Int,
        periodType: String,
        moneyType: String
    ) {
        val params = textView.layoutParams
        val parentView = textView.parent as ViewGroup
        val textViewWidth = parentView.measuredWidth - binding.tvMypage2weeks1Year.width
        val width = getGraphAnimationWidth(textViewWidth, periodType)
        textView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val animator = ValueAnimator.ofInt(0, width).apply {
                        duration = 1000
                        addUpdateListener { valueAnimator ->
                            params?.width = valueAnimator.animatedValue as Int
                            textView.requestLayout()
                        }
                        doOnStart {
                            textView.text = ""
                        }
                        doOnEnd {
                            textView.text = when (moneyType) {
                                "SAVE" -> {
                                    String.format(
                                        getString(R.string.mypage_save_money),
                                        formatWithCommaForMoney(amount)
                                    )
                                }

                                "SPEND" -> {
                                    String.format(
                                        getString(R.string.mypage_spend_money),
                                        formatWithCommaForMoney(amount)
                                    )
                                }

                                else -> {
                                    ""
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

    private val is2weekGraphAnimating = mutableMapOf<Int, Boolean>()

    fun startAnimationOnVisible(
        scrollView: ScrollView,
        textView: TextView,
        animator: ValueAnimator
    ) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val textViewId = textView.id
            if (is2weekGraphAnimating[textViewId] == true) {
                // 이 TextView에 대한 애니메이션이 이미 실행 중일때는 무시
                return@addOnScrollChangedListener
            }
            val scrollY = scrollView.scrollY
            val location = IntArray(2)
            textView.getLocationOnScreen(location)
            if (location[1] > scrollY && location[1] + textView.height < scrollY + scrollView.height) {
                // 사용자의 스크롤 위치가 textView를 완전히 보는 경우에 위치했을때, 애니메이션 시작
                is2weekGraphAnimating[textViewId] = true
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // 애니메이션이 끝난 경우 false로 바꿔주어 animator.start의 중복 실행 방지
                        is2weekGraphAnimating[textViewId] = false
                    }
                })
                animator.start()
            }
        }
    }

    private fun setMyPageWorkHoursAndType(
        textView: TextView,
        money: Int,
        moneyType: String,
        textType: String
    ) {
        var amountText = ""
        var fullText = ""
        when (textType) {
            "WORK" -> {
                amountText = (money / 9860).toString() + "시간"
                fullText = getString(R.string.mypage_2weeks_save_for_job, amountText)
            }

            "1YEAR" -> {
                amountText = formatWithCommaForMoney(money * 24) + "원"
                when (moneyType) {
                    "SAVE" -> {
                        fullText = getString(R.string.mypage_2weeks_save_for_1year, amountText)
                    }

                    "SPEND" -> {
                        fullText = getString(R.string.mypage_2weeks_spend_for_1year, amountText)
                    }
                }
            }
        }
        val startIndex = fullText.indexOf(amountText)
        val endIndex = startIndex + amountText.length

        val spannableString = SpannableString(fullText).apply {
            when (moneyType) {
                "SAVE" -> {
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

                "SPEND" -> {
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
    }
}
