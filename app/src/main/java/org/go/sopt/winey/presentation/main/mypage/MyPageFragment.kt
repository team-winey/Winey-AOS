package org.go.sopt.winey.presentation.main.mypage

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import org.go.sopt.winey.presentation.main.mypage.myfeed.MyFeedFragment
import org.go.sopt.winey.presentation.main.notification.NotificationActivity
import org.go.sopt.winey.presentation.nickname.NicknameActivity
import org.go.sopt.winey.presentation.onboarding.guide.GuideActivity
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.binding.BindingFragment
import org.go.sopt.winey.util.fragment.snackBar
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
    private var isNotificationPermissionAllowed = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amplitudeUtils.logEvent("view_mypage")
        initCheckNotificationPermission()

        initUserData()
        initNavigation()

        addListener()
        addObserver()

        checkFromWineyFeed()
    }

    private fun addListener() {
        initEditNicknameButtonClickListener()
        initMyFeedButtonClickListener()
        registerBackPressedCallback()
    }

    private fun addObserver() {
        setupGetUserState()
        setupDeleteUserState()
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

    private fun navigateToNotificationSetting(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setNotificationIntentActionOreo(context)
        } else {
            setNorificationIntentActionOreoLess(context)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun setNotificationIntentActionOreo(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    private fun setNorificationIntentActionOreoLess(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo?.uid)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    private fun patchUserInfo() {
        lifecycleScope.launch {
            val data = dataStoreRepository.getUserInfo().first()
            val newData = data?.copy(fcmIsAllowed = false)
            dataStoreRepository.saveUserInfo(newData)
        }
    }

    // 닉네임 액티비티 갔다가 다시 돌아왔을 때 유저 데이터 갱신하도록
    override fun onStart() {
        super.onStart()
        mainViewModel.getUser()
        initCheckNotificationPermission()
    }

    private fun checkFromWineyFeed() {
        val isFromWineyFeed = arguments?.getBoolean(KEY_FROM_WINEY_FEED)
        if (isFromWineyFeed == true) {
            showTargetSettingBottomSheet()
        }
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
                navigateAndBackStack<MyFeedFragment>()
                arguments?.clear()
            }
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
            putExtra(KEY_PREV_SCREEN_NAME, VAL_MY_PAGE_SCREEN)
            startActivity(this)
        }
    }

    private fun navigateToMyFeedScreen() {
        navigateAndBackStack<MyFeedFragment>()
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
        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"
        private const val VAL_MY_PAGE_SCREEN = "MyPageFragment"
        private const val KEY_FROM_NOTI = "fromNoti"
        private const val KEY_FROM_WINEY_FEED = "fromWineyFeed"
        private const val KEY_TO_MYFEED = "toMyFeed"
    }
}
