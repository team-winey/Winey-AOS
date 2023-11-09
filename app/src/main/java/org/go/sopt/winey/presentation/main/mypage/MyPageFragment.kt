package org.go.sopt.winey.presentation.main.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.FragmentMyPageBinding
import org.go.sopt.winey.domain.entity.User
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainViewModel
import org.go.sopt.winey.presentation.main.mypage.myfeed.MyFeedFragment
import org.go.sopt.winey.presentation.main.notification.NotificationActivity
import org.go.sopt.winey.presentation.model.WineyDialogLabel
import org.go.sopt.winey.presentation.nickname.NicknameActivity
import org.go.sopt.winey.presentation.onboarding.guide.GuideActivity
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.WineyDialogFragment
import org.go.sopt.winey.util.fragment.snackBar
import org.go.sopt.winey.util.fragment.stringOf
import org.go.sopt.winey.util.fragment.viewLifeCycle
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.setOnSingleClickListener
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val myPageViewModel by viewModels<MyPageViewModel>()

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amplitudeUtils.logEvent("view_mypage")

        initUserData()
        initNavigation()

        init1On1ButtonClickListener()
        initTermsButtonClickListener()
        initLevelHelpButtonClickListener()
        initToMyFeedButtonClickListener()
        initLogoutButtonClickListener()
        initWithdrawButtonClickListener()
        initNicknameButtonClickListener()

        registerBackPressedCallback()
        setupGetUserState()
        setupDeleteUserState()
        checkFromWineyFeed()
    }

    // 닉네임 액티비티 갔다가 다시 돌아왔을 때 유저 데이터 갱신하도록
    override fun onStart() {
        super.onStart()
        mainViewModel.getUser()
    }

    // 위니피드 프래그먼트에서 마이페이지로 전환 시, 바텀시트 바로 띄우도록
    private fun checkFromWineyFeed() {
        val isFromWineyFeed = arguments?.getBoolean(KEY_FROM_WINEY_FEED)
        if (isFromWineyFeed == true) {
            showTargetSettingBottomSheet()
        }
    }

    // 마이페이지 왔다가 다시 알림 화면으로 돌아가도록
    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val receivedBundle = arguments
                if (receivedBundle != null) {
                    val isFromNotification = receivedBundle.getBoolean(KEY_FROM_NOTI)
                    if (isFromNotification) {
                        val intent = Intent(requireContext(), NotificationActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initUserData() {
        viewLifeCycleScope.launch {
            val data = dataStoreRepository.getUserInfo().first()
            if (data != null) {
                updateUserInfo(data)
                initTargetModifyButtonClickListener(data)
            }
        }
    }

    private fun initNicknameButtonClickListener() {
        binding.ivMypageNickname.setOnClickListener {
            amplitudeUtils.logEvent("click_edit_nickname")
            navigateToNicknameScreen()
        }
    }

    private fun initToMyFeedButtonClickListener() {
        binding.clMypageToMyfeed.setOnSingleClickListener {
            amplitudeUtils.logEvent("click_myfeed")
            navigateAndBackStack<MyFeedFragment>()
        }
    }

    private fun initNavigation() {
        val receivedBundle = arguments
        if (receivedBundle != null) {
            val value = receivedBundle.getBoolean(KEY_TO_MYFEED)
            if (value) {
                navigateAndBackStack<MyFeedFragment>()
                arguments?.clear()
            }
        }
    }

    private fun init1On1ButtonClickListener() {
        binding.clMypageTo1on1.setOnClickListener {
            val url = ONE_ON_ONE_URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun initTermsButtonClickListener() {
        binding.clMypageToTerms.setOnClickListener {
            val url = TERMS_URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun initLevelHelpButtonClickListener() {
        binding.btnMypageLevelHelp.setOnClickListener {
            amplitudeUtils.logEvent("click_info")
            val intent = Intent(context, MypageHelpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initLogoutButtonClickListener() {
        binding.clMypageLogout.setOnClickListener {
            amplitudeUtils.logEvent("click_logout")
            val dialog = WineyDialogFragment.newInstance(
                WineyDialogLabel(
                    stringOf(R.string.mypage_logout_dialog_title),
                    stringOf(R.string.mypage_logout_dialog_subtitle),
                    stringOf(R.string.mypage_logout_dialog_negative_button),
                    stringOf(R.string.mypage_logout_dialog_positive_button)
                ),
                handleNegativeButton = {},
                handlePositiveButton = { mainViewModel.postLogout() }
            )
            dialog.show(parentFragmentManager, TAG_LOGOUT_DIALOG)
        }
    }

    private fun initWithdrawButtonClickListener() {
        binding.tvMypageWithdraw.setOnClickListener {
            val dialog = WineyDialogFragment.newInstance(
                WineyDialogLabel(
                    stringOf(R.string.mypage_withdraw_dialog_title),
                    stringOf(R.string.mypage_withdraw_dialog_subtitle),
                    stringOf(R.string.mypage_withdraw_dialog_negative_button),
                    stringOf(R.string.mypage_withdraw_dialog_positive_button)
                ),
                handleNegativeButton = { myPageViewModel.deleteUser() },
                handlePositiveButton = {}
            )
            dialog.show(parentFragmentManager, TAGE_WITHDRAW_DIALOG)
        }
    }

    private fun setupDeleteUserState() {
        myPageViewModel.deleteUserState.flowWithLifecycle(viewLifeCycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        myPageViewModel.clearDataStore()
                        navigateToGuideScreen()
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {
                    }
                }
            }.launchIn(viewLifeCycleScope)
    }

    private fun navigateToGuideScreen() {
        Intent(requireContext(), GuideActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    private fun navigateToNicknameScreen() {
        Intent(requireContext(), NicknameActivity::class.java).apply {
            putExtra(KEY_PREV_SCREEN, VALUE_MY_PAGE_SCREEN)
            startActivity(this)
        }
    }

    private fun setupGetUserState() {
        mainViewModel.getUserState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val data = dataStoreRepository.getUserInfo().first() ?: return@onEach
                    updateUserInfo(data)
                    initTargetModifyButtonClickListener(data)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                else -> {}
            }
        }.launchIn(lifecycleScope)
    }

    private fun updateUserInfo(data: User) {
        binding.data = data
        updateTargetInfo(data)
        updateUserLevel(data)
    }

    private fun updateTargetInfo(data: User) {
        // 목표를 설정한 적이 없거나, 기간이 종료된 경우
        // 아직 없어요 뷰 띄우기
        if (data.isOver) {
            binding.tvMypageTargetAmount.text = getString(R.string.mypage_not_yet_set)
            binding.tvMypagePeriodValue.text = getString(R.string.mypage_not_yet_set)
        } else {
            binding.targetMoney = data
            if (data.dday == 0) {
                binding.tvMypagePeriodValue.text = getString(R.string.mypage_d_day)
            } else {
                binding.dday = data
            }
        }
    }

    private fun updateUserLevel(data: User) {
        when (data.userLevel) {
            LEVEL_COMMON -> {
                binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv1_progressbar)
                binding.ivMypageProfile.setImageResource(R.drawable.ic_mypage_lv1_profile)
            }

            LEVEL_KNIGHT -> {
                binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv2_progressbar)
                binding.ivMypageProfile.setImageResource(R.drawable.ic_mypage_lv2_profile)
            }

            LEVEL_NOBLESS -> {
                binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv3_progressbar)
                binding.ivMypageProfile.setImageResource(R.drawable.ic_mypage_lv3_profile)
            }

            LEVEL_KING -> {
                binding.ivMypageProgressbar.setImageResource(R.drawable.ic_mypage_lv4_progressbar)
                binding.ivMypageProfile.setImageResource(R.drawable.ic_mypage_lv4_profile)
            }
        }
    }

    private fun initTargetModifyButtonClickListener(user: User) {
        binding.clMypageTargetmoney.setOnSingleClickListener {
            amplitudeUtils.logEvent("click_goalsetting")

            // 목표를 설정한 적 없거나, 기간이 종료되었거나, 기간 내 목표를 달성한 경우
            // 바텀 시트 활성화
            if (user.isOver || user.isAttained) {
                showTargetSettingBottomSheet()
            } else {
                showTargetNotOverDialog()
            }
        }
    }

    private fun showTargetSettingBottomSheet() {
        val bottomSheet = TargetAmountBottomSheetFragment()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        amplitudeUtils.logEvent("view_goalsetting")
    }

    private fun showTargetNotOverDialog() {
        val dialog = MyPageNotOverDialogFragment()
        dialog.show(parentFragmentManager, dialog.tag)
    }

    private inline fun <reified T : Fragment> navigateAndBackStack() {
        parentFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.simpleName)
            addToBackStack(null)
        }
    }

    companion object {
        private const val LEVEL_COMMON = "평민"
        private const val LEVEL_KNIGHT = "기사"
        private const val LEVEL_NOBLESS = "귀족"
        private const val LEVEL_KING = "황제"
        private const val ONE_ON_ONE_URL = "https://open.kakao.com/o/s751Susf"
        private const val TERMS_URL =
            "https://empty-weaver-a9f.notion.site/iney-9dbfe130c7df4fb9a0903481c3e377e6?pvs=4"
        private const val TAG_LOGOUT_DIALOG = "LOGOUT_DIALOG"
        private const val TAGE_WITHDRAW_DIALOG = "WITHDRAW_DIALOG"

        private const val KEY_PREV_SCREEN = "PREV_SCREEN_NAME"
        private const val VALUE_MY_PAGE_SCREEN = "MyPageFragment"
        private const val KEY_FROM_NOTI = "fromNoti"
        private const val KEY_FROM_WINEY_FEED = "fromWineyFeed"
        private const val KEY_TO_MYFEED = "toMyFeed"
    }
}
