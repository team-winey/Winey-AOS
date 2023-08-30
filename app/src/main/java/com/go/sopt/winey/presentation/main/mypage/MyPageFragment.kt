package com.go.sopt.winey.presentation.main.mypage

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
import com.go.sopt.winey.R
import com.go.sopt.winey.databinding.FragmentMyPageBinding
import com.go.sopt.winey.domain.entity.User
import com.go.sopt.winey.domain.repository.DataStoreRepository
import com.go.sopt.winey.presentation.main.MainViewModel
import com.go.sopt.winey.presentation.main.mypage.myfeed.MyFeedFragment
import com.go.sopt.winey.presentation.main.notification.NotificationActivity
import com.go.sopt.winey.presentation.nickname.NicknameActivity
import com.go.sopt.winey.presentation.onboarding.guide.GuideActivity
import com.go.sopt.winey.util.amplitude.AmplitudeUtils
import com.go.sopt.winey.util.binding.BindingFragment
import com.go.sopt.winey.util.fragment.WineyDialogFragment
import com.go.sopt.winey.util.fragment.snackBar
import com.go.sopt.winey.util.fragment.stringOf
import com.go.sopt.winey.util.fragment.viewLifeCycle
import com.go.sopt.winey.util.fragment.viewLifeCycleScope
import com.go.sopt.winey.util.view.UiState
import com.go.sopt.winey.util.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

        init1On1ButtonClickListener()
        initTermsButtonClickListener()
        initLevelHelpButtonClickListener()
        initToMyFeedButtonClickListener()
        initLogoutButtonClickListener()
        initWithdrawButtonClickListener()
        initNicknameButtonClickListener()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        setupGetUserState()
        setupDeleteUserState()
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val receivedBundle = arguments
            if (receivedBundle != null) {
                val value = receivedBundle.getString("fromNoti")
                if (value == "true") {
                    val intent = Intent(requireContext(), NotificationActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            requireActivity().finish()
        }
    }

    // 닉네임 액티비티 갔다가 다시 돌아왔을 때 유저 데이터 갱신하도록
    override fun onStart() {
        super.onStart()
        mainViewModel.getUser()
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
            val dialog = WineyDialogFragment(
                stringOf(R.string.mypage_logout_dialog_title),
                stringOf(R.string.mypage_logout_dialog_subtitle),
                stringOf(R.string.mypage_logout_dialog_negative_button),
                stringOf(R.string.mypage_logout_dialog_positive_button),
                handleNegativeButton = {},
                handlePositiveButton = { mainViewModel.postLogout() }
            )
            dialog.show(parentFragmentManager, TAG_LOGOUT_DIALOG)
        }
    }

    private fun initWithdrawButtonClickListener() {
        binding.tvMypageWithdraw.setOnClickListener {
            val dialog = WineyDialogFragment(
                stringOf(R.string.mypage_withdraw_dialog_title),
                stringOf(R.string.mypage_withdraw_dialog_subtitle),
                stringOf(R.string.mypage_withdraw_dialog_negative_button),
                stringOf(R.string.mypage_withdraw_dialog_positive_button),
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
            putExtra(EXTRA_KEY, EXTRA_VALUE)
            startActivity(this)
        }
    }

    private fun setupGetUserState() {
        mainViewModel.getUserState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    val data = dataStoreRepository.getUserInfo().first()
                    updateUserInfo(data)
                    initBottomSheetClickListener(data)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                is UiState.Empty -> {
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun initBottomSheetClickListener(data: User?) {
        binding.clMypageTargetmoney.setOnSingleClickListener {
            amplitudeUtils.logEvent("click_goalsetting")

            when (data?.isOver) {
                true -> {
                    val bottomSheet = TargetAmountBottomSheetFragment()
                    bottomSheet.show(this.childFragmentManager, bottomSheet.tag)
                    amplitudeUtils.logEvent("view_goalsetting")
                }

                false -> {
                    val dialog = MyPageDialogFragment()
                    dialog.show(this.childFragmentManager, dialog.tag)
                }

                null -> {
                }
            }
        }
    }

    private fun updateUserInfo(data: User?) {
        binding.data = data

        // todo: 기간 내 목표 달성하면 누적위니, 위니횟수도 0으로 초기화 되도록

        handleIsOver(data)
        handleUserLevel(data)
    }

    private fun handleIsOver(data: User?) {
        if (data == null) return

        if (data.isOver) {
            binding.tvMypageTargetAmount.text = getString(R.string.mypage_not_yet_set)
            binding.tvMypagePeriodValue.text = getString(R.string.mypage_not_yet_set)
        } else {
            if (data.dday == 0) {
                binding.tvMypagePeriodValue.text = getString(R.string.mypage_d_day)
                binding.targetMoney = data
            } else {
                binding.targetMoney = data
                binding.dday = data
            }
        }
    }

    private fun handleUserLevel(data: User?) {
        when (data?.userLevel) {
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
        private const val EXTRA_KEY = "PREV_SCREEN_NAME"
        private const val EXTRA_VALUE = "MyPageFragment"
        private const val TAG_LOGOUT_DIALOG = "LOGOUT_DIALOG"
        private const val TAGE_WITHDRAW_DIALOG = "WITHDRAW_DIALOG"
    }
}
