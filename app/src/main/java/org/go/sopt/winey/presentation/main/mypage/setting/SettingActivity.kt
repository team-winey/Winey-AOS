package org.go.sopt.winey.presentation.main.mypage.setting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivitySettingBinding
import org.go.sopt.winey.domain.entity.User
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.presentation.main.MainViewModel
import org.go.sopt.winey.presentation.model.WineyDialogLabel
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.snackBar
import org.go.sopt.winey.util.context.stringOf
import org.go.sopt.winey.util.fragment.WineyDialogFragment
import org.go.sopt.winey.util.view.UiState
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : BindingActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    private val settingViewModel by viewModels<SettingViewModel>()
    private val mainViewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var amplitudeUtils: AmplitudeUtils
    private var isNotificationPermissionAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addListener()
        addObserver()
        initNotificationPermissionState()
    }

    override fun onStart() {
        super.onStart()
        initNotificationPermissionState()
    }

    private fun addListener() {
        init1On1ButtonClickListener()
        initLogoutButtonClickListener()
        initTermsButtonClickListener()
        initWithdrawButtonClickListener()
        initNotiPermissionButtonClickListener()
    }

    private fun addObserver() {

    }

    private fun switchOnNotification() {
        binding.ivSettingAgree.transitionToState(R.id.end, -1)
        patchUserInfo()
        settingViewModel.patchAllowedNotification(isAllowed = false)
    }

    private fun switchOffNotification() {
        binding.ivSettingAgree.transitionToState(R.id.start, -1)
        patchUserInfo()
        settingViewModel.patchAllowedNotification(isAllowed = true)
    }

    private fun initNotiPermissionButtonClickListener() {
        binding.llSettingAgreePermissionChange.setOnClickListener {
            showSystemNotificationSetting()
        }
    }

    private fun showSystemNotificationSetting() {
        Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, this)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    private fun showNotificationOffConfirmDialog() {
        val dialog = WineyDialogFragment.newInstance(
            WineyDialogLabel(
                stringOf(R.string.notification_off_dialog_title),
                stringOf(R.string.notification_off_dialog_subtitle),
                stringOf(R.string.notification_off_dialog_negative_button),
                stringOf(R.string.notification_off_dialog_positive_button)
            ),
            handleNegativeButton = {},
            handlePositiveButton = { switchOffNotification() }
        )
        dialog.show(
            supportFragmentManager,
            TAG_NOTIFICATION_OFF_DIALOG
        )
    }

    private fun updateNotificationAllowSwitchState(data: User) {
        if (isNotificationPermissionAllowed) {
            binding.ivSettingAgree.isVisible = true
            binding.llSettingAgreePermissionChange.isGone = true
            binding.tvSettingAgreePermission.isGone = true
            when (data.fcmIsAllowed) {
                true -> {
                    binding.ivSettingAgree.transitionToState(R.id.end, 1)
                }

                false -> {
                    binding.ivSettingAgree.transitionToState(R.id.start, 1)
                }
            }
        } else {
            binding.ivSettingAgree.isGone = true
            binding.llSettingAgreePermissionChange.isVisible = true
            binding.tvSettingAgreePermission.isVisible = true
        }
    }

    private fun patchUserInfo() {
        lifecycleScope.launch {
            val data = dataStoreRepository.getUserInfo().first()
            val newData = data?.copy(fcmIsAllowed = false)
            dataStoreRepository.saveUserInfo(newData)
        }
    }

    private fun initNotiToggleButtonClickListener() {
        binding.ivSettingSwitch.setOnClickListener {
            val isAllowed = when (binding.ivSettingAgree.currentState) {
                R.id.start -> false
                R.id.end -> true
                else -> false
            }

            if (!isAllowed) {
                switchOnNotification()
            } else {
                showNotificationOffConfirmDialog()
            }
        }
    }

    private fun initNotificationPermissionState() {
        isNotificationPermissionAllowed =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
    }

    private fun init1On1ButtonClickListener() {
        binding.clSettingTo1on1.setOnClickListener {
            val url = ONE_ON_ONE_URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun initTermsButtonClickListener() {
        binding.clSettingToTerms.setOnClickListener {
            val url = TERMS_URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun initLogoutButtonClickListener() {
        binding.clSettingLogout.setOnClickListener {
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
            dialog.show(supportFragmentManager, TAG_LOGOUT_DIALOG)
        }
    }

    private fun initWithdrawButtonClickListener() {
        binding.clSettingWithdraw.setOnClickListener {
            val dialog = WineyDialogFragment.newInstance(
                WineyDialogLabel(
                    stringOf(R.string.mypage_withdraw_dialog_title),
                    stringOf(R.string.mypage_withdraw_dialog_subtitle),
                    stringOf(R.string.mypage_withdraw_dialog_negative_button),
                    stringOf(R.string.mypage_withdraw_dialog_positive_button)
                ),
                handleNegativeButton = { settingViewModel.deleteUser() },
                handlePositiveButton = {}
            )
            dialog.show(supportFragmentManager, TAG_WITHDRAW_DIALOG)
        }
    }

    private fun setupDeleteUserState() {
        settingViewModel.deleteUserState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        settingViewModel.clearDataStore()
                        //navigateToGuideScreen()
                    }

                    is UiState.Failure -> {
                        snackBar(binding.root) { state.msg }
                    }

                    else -> {
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun setupPatchAllowedNotificationState() {
        settingViewModel.patchAllowedNotificationState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        when (state.data) {
                            true -> {
                                binding.ivSettingAgree.transitionToState(R.id.end, -1)
                            }

                            false -> {
                                binding.ivSettingAgree.transitionToState(R.id.start, -1)
                            }

                            null -> {
                                binding.ivSettingAgree.transitionToState(R.id.start, -1)
                            }
                        }
                    }

                    else -> {}
                }
            }
    }

    companion object {

        private const val ONE_ON_ONE_URL = "https://open.kakao.com/o/s751Susf"
        private const val TERMS_URL =
            "https://empty-weaver-a9f.notion.site/iney-9dbfe130c7df4fb9a0903481c3e377e6?pvs=4"

        private const val TAG_LOGOUT_DIALOG = "LOGOUT_DIALOG"
        private const val TAG_WITHDRAW_DIALOG = "WITHDRAW_DIALOG"

        private const val TAG_NOTIFICATION_OFF_DIALOG = "offNotification"
    }
}
