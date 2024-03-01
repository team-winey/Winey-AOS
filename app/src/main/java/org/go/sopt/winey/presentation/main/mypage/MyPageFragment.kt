package org.go.sopt.winey.presentation.main.mypage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
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
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainViewModel
import org.go.sopt.winey.presentation.main.mypage.myfeed.MyFeedActivity
import org.go.sopt.winey.presentation.main.mypage.setting.SettingActivity
import org.go.sopt.winey.presentation.main.notification.NotificationActivity
import org.go.sopt.winey.presentation.nickname.NicknameActivity
import org.go.sopt.winey.presentation.onboarding.guide.GuideActivity
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.snackBar
import org.go.sopt.winey.util.fragment.viewLifeCycleScope
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.setOnSingleClickListener
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val mainViewModel by activityViewModels<MainViewModel>()

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils
    private var isNotificationPermissionAllowed = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amplitudeUtils.logEvent("view_mypage")

        initCheckNotificationPermission()
        initUserData()
        initNavigation()
        addListener()
        addObserver()
    }

    private fun addListener() {
        initEditNicknameButtonClickListener()
        initMyFeedButtonClickListener()
        initSettingButtonClickListener()
        registerBackPressedCallback()
    }

    private fun addObserver() {
        setupGetUserState()
    }

    private fun initCheckNotificationPermission() {
        isNotificationPermissionAllowed =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
    }

    // 닉네임 액티비티 갔다가 다시 돌아왔을 때 유저 데이터 갱신하도록
    override fun onStart() {
        super.onStart()
        mainViewModel.getUser()
        initCheckNotificationPermission()
    }

    private fun initEditNicknameButtonClickListener() {
        binding.ivMypageEditNickname.setOnSingleClickListener {
            navigateToNicknameScreen()
        }
    }

    private fun initMyFeedButtonClickListener() {
        binding.btnMypageMyfeed.setOnSingleClickListener {
            navigateToMyFeedScreen()
        }
    }

    private fun initSettingButtonClickListener() {
        binding.ivMypageSetting.setOnClickListener {
            navigateToSettingScreen()
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
            }
        }
    }

    private fun initNavigation() {
        val receivedBundle = arguments
        if (receivedBundle != null) {
            val value = receivedBundle.getBoolean(KEY_TO_MYFEED)
            if (value) {
                navigateToMyFeedScreen()
                arguments?.clear()
            }
        }
    }

    private fun navigateToNicknameScreen() {
        Intent(requireContext(), NicknameActivity::class.java).apply {
            putExtra(KEY_PREV_SCREEN_NAME, VAL_MY_PAGE_SCREEN)
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

    private fun setupGetUserState() {
        mainViewModel.getUserState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    val data = dataStoreRepository.getUserInfo().first() ?: return@onEach
                    updateUserInfo(data)
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

    private fun showTargetSettingBottomSheet() {
        val bottomSheet = TargetAmountBottomSheetFragment()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        amplitudeUtils.logEvent("view_goalsetting")
    }

    companion object {
        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"
        private const val VAL_MY_PAGE_SCREEN = "MyPageFragment"
        private const val KEY_FROM_NOTI = "fromNoti"
        private const val KEY_TO_MYFEED = "toMyFeed"
    }
}
