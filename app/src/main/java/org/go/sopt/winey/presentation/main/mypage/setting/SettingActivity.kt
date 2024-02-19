package org.go.sopt.winey.presentation.main.mypage.setting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivitySettingBinding
import org.go.sopt.winey.presentation.main.MainViewModel
import org.go.sopt.winey.presentation.model.WineyDialogLabel
import org.go.sopt.winey.util.amplitude.AmplitudeUtils
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.stringOf
import org.go.sopt.winey.util.fragment.WineyDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : BindingActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    private val settingViewModel by viewModels<SettingViewModel>()
    private val mainViewModel by viewModels<MainViewModel>()

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
        updateNotificationToggleByPermission()
    }

    private fun addListener() {
        init1On1ButtonClickListener()
        initLogoutButtonClickListener()
        initTermsButtonClickListener()
        initWithdrawButtonClickListener()
    }

    private fun addObserver() {

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

    private fun updateNotificationToggleByPermission() {
        if (isNotificationPermissionAllowed) {
            binding.ivSettingAgree.isVisible = true

            binding.llSettingAgreePermissionChange.isGone = true
            binding.tvSettingAgreePermission.isGone = true
        } else {
            binding.ivSettingAgree.isGone = true

            binding.llSettingAgreePermissionChange.isVisible = true
            binding.tvSettingAgreePermission.isVisible = true
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

    companion object {

        private const val ONE_ON_ONE_URL = "https://open.kakao.com/o/s751Susf"
        private const val TERMS_URL =
            "https://empty-weaver-a9f.notion.site/iney-9dbfe130c7df4fb9a0903481c3e377e6?pvs=4"

        private const val TAG_LOGOUT_DIALOG = "LOGOUT_DIALOG"
        private const val TAG_WITHDRAW_DIALOG = "WITHDRAW_DIALOG"
    }
}
