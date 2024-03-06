package org.go.sopt.winey.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityMainBinding
import org.go.sopt.winey.presentation.main.feed.WineyFeedFragment
import org.go.sopt.winey.presentation.main.feed.detail.DetailActivity
import org.go.sopt.winey.presentation.main.mypage.MyPageFragment
import org.go.sopt.winey.presentation.main.mypage.goal.GoalPathActivity
import org.go.sopt.winey.presentation.main.recommend.RecommendFragment
import org.go.sopt.winey.presentation.model.NotificationType
import org.go.sopt.winey.presentation.onboarding.login.LoginActivity
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.snackBar
import org.go.sopt.winey.util.context.stringOf
import org.go.sopt.winey.util.context.wineySnackbar
import org.go.sopt.winey.util.view.UiState
import org.go.sopt.winey.util.view.snackbar.SnackbarType

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel by viewModels<MainViewModel>()

    private val isUploadSuccess by lazy { intent.getBooleanExtra(KEY_FEED_UPLOAD, false) }
    private val isDeleteSuccess by lazy { intent.getBooleanExtra(KEY_FEED_DELETE, false) }
    private val levelUpFromUpload by lazy {
        intent.getBooleanExtra(
            WineyFeedFragment.KEY_LEVEL_UP,
            false
        )
    }

    private val notiType by lazy { intent.getStringExtra(KEY_NOTI_TYPE) }
    private val feedId by lazy { intent.getStringExtra(KEY_FEED_ID) }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                wineySnackbar(
                    anchorView = binding.root,
                    message = stringOf(R.string.snackbar_noti_permission_denied),
                    type = SnackbarType.NotiPermission(
                        onActionClicked = { showSystemNotificationSetting() }
                    )
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermission()
        initNotiTypeHandler()

        viewModel.apply {
            getUser() // 위니피드, 마이페이지 프래그먼트에서 관찰
            patchFcmToken()
            saveLevelUpState(levelUpFromUpload)
        }

        addListener()
        addObserver()

        initFragment()
        showWineyFeedResultSnackBar()
    }

    private fun addListener() {
        initBnvItemSelectedListener()
    }

    private fun addObserver() {
        setupLogoutState()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun showSystemNotificationSetting() {
        Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    private fun initNotiTypeHandler() {
        val notificationType = NotificationType.values().find { it.key == notiType }
        when (notificationType) {
            NotificationType.RANK_UP_TO_2, NotificationType.RANK_UP_TO_3,
            NotificationType.RANK_UP_TO_4, NotificationType.RANK_DOWN_TO_1,
            NotificationType.RANK_DOWN_TO_2, NotificationType.RANK_DOWN_TO_3,
            NotificationType.GOAL_FAILED -> {
                navigateTo(
                    fragment = MyPageFragment.newInstance(),
                    args = Bundle().apply {
                        putBoolean(KEY_FROM_NOTI, true)
                    }
                )
            }

            NotificationType.LIKE_NOTIFICATION, NotificationType.COMMENT_NOTIFICATION ->
                navigateToDetailScreen(feedId?.toInt())

            NotificationType.HOW_TO_LEVEL_UP -> navigateToGoalPathScreen()
            else -> {}
        }
    }

    private fun initFragment() {
        if (intent.getBooleanExtra(KEY_FROM_GOAL_PATH, false)) {
            navigateTo(MyPageFragment.newInstance())
            syncBnvSelectedItem(R.id.menu_mypage)
            return
        }

        if (intent.getBooleanExtra(KEY_TO_MY_PAGE, false)) {
            navigateTo(
                fragment = MyPageFragment.newInstance(),
                args = Bundle().apply {
                    putBoolean(KEY_FROM_NOTI, true)
                }
            )
            syncBnvSelectedItem(R.id.menu_mypage)
            return
        }

        navigateTo(WineyFeedFragment.newInstance())
    }

    private fun syncBnvSelectedItem(@IdRes selectedItemId: Int) {
        binding.bnvMain.selectedItemId = selectedItemId
    }

    private fun initBnvItemSelectedListener() {
        binding.bnvMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_feed -> navigateTo(WineyFeedFragment.newInstance())
                R.id.menu_recommend -> navigateTo(RecommendFragment.newInstance())
                R.id.menu_mypage -> navigateTo(MyPageFragment.newInstance())
            }
            true
        }
    }

    private fun showWineyFeedResultSnackBar() {
        if (isUploadSuccess) {
            wineySnackbar(
                anchorView = binding.root,
                message = stringOf(R.string.snackbar_upload_success),
                type = SnackbarType.WineyFeedResult(isSuccess = true)
            )
        }

        if (isDeleteSuccess) {
            wineySnackbar(
                anchorView = binding.root,
                message = stringOf(R.string.snackbar_feed_delete_success),
                type = SnackbarType.WineyFeedResult(isSuccess = true)
            )
        }
    }

    private fun setupLogoutState() {
        viewModel.logoutState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    navigateToLoginScreen()
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                is UiState.Empty -> {
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun navigateTo(fragment: Fragment, args: Bundle? = null) {
        args?.let {
            fragment.arguments = it
        }

        supportFragmentManager.commit {
            replace(R.id.fcv_main, fragment)
        }
    }

    private fun navigateToLoginScreen() {
        Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(this)
            finish()
        }
    }

    private fun navigateToDetailScreen(feedId: Int?) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(KEY_FEED_ID, feedId)
        startActivity(intent)
    }

    private fun navigateToGoalPathScreen() {
        val intent = Intent(this, GoalPathActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val KEY_FEED_UPLOAD = "upload"
        private const val KEY_FEED_DELETE = "delete"
        private const val KEY_NOTI_TYPE = "notiType"
        private const val KEY_FROM_NOTI = "fromNoti"

        const val KEY_FEED_ID = "feedId"
        const val KEY_TO_MY_PAGE = "navigateMyPage"
        const val KEY_FROM_GOAL_PATH = "fromGoalPath"
    }
}
