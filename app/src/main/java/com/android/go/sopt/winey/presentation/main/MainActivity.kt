package com.android.go.sopt.winey.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityMainBinding
import com.android.go.sopt.winey.presentation.main.feed.WineyFeedFragment
import com.android.go.sopt.winey.presentation.main.mypage.MyPageFragment
import com.android.go.sopt.winey.presentation.main.recommend.RecommendFragment
import com.android.go.sopt.winey.presentation.onboarding.login.LoginActivity
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.snackBar
import com.android.go.sopt.winey.util.context.stringOf
import com.android.go.sopt.winey.util.context.wineySnackbar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel by viewModels<MainViewModel>()
    private val isUploadSuccess by lazy { intent.extras?.getBoolean(EXTRA_UPLOAD_KEY, false) }
    private val isDeleteSuccess by lazy { intent.extras?.getBoolean(EXTRA_DELETE_KEY, false) }
    private val isReportSuccess by lazy { intent.extras?.getBoolean(EXTRA_REPORT_KEY, false) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 위니피드, 마이페이지 프래그먼트에서 getUserState 관찰
        mainViewModel.getUser()

        initFragment()
        initBnvItemSelectedListener()
        syncBottomNavigationSelection()

        setupLogoutState()
        showSuccessSnackBar()
    }

    private fun initFragment() {
        if (intent.getBooleanExtra("navigateMypage", false)) {
            navigateTo<MyPageFragment>()
            binding.bnvMain.selectedItemId = R.id.menu_mypage
        } else {
            navigateTo<WineyFeedFragment>()
        }
    }

    private fun showSuccessSnackBar(){
        if (isUploadSuccess != null && isUploadSuccess == true) {
            wineySnackbar(binding.root, true, stringOf(R.string.snackbar_upload_success))
        }
        if (isDeleteSuccess != null && isDeleteSuccess == true) {
            wineySnackbar(binding.root, true, stringOf(R.string.snackbar_feed_delete_success))
        }
        if (isReportSuccess != null && isReportSuccess == true) {
            wineySnackbar(binding.root, true, stringOf(R.string.snackbar_report_success))
        }
    }

    private fun initBnvItemSelectedListener() {
        binding.bnvMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_feed -> navigateTo<WineyFeedFragment>()
                R.id.menu_recommend -> navigateTo<RecommendFragment>()
                R.id.menu_mypage -> navigateTo<MyPageFragment>()
            }
            true
        }
    }

    private fun syncBottomNavigationSelection() {
        supportFragmentManager.addOnBackStackChangedListener {
            syncBottomNavigation()
        }
    }

    private fun syncBottomNavigation() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fcv_main)
        when (currentFragment) {
            is WineyFeedFragment -> binding.bnvMain.selectedItemId = R.id.menu_feed
            is RecommendFragment -> binding.bnvMain.selectedItemId = R.id.menu_recommend
            is MyPageFragment -> binding.bnvMain.selectedItemId = R.id.menu_mypage
        }
    }

    private fun setupLogoutState() {
        mainViewModel.logoutState.flowWithLifecycle(lifecycle).onEach { state ->
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

    private fun navigateToLoginScreen() {
        Intent(this@MainActivity, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(this)
            finish()
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.simpleName)
        }
    }

    companion object {
        private const val EXTRA_UPLOAD_KEY = "upload"
        private const val EXTRA_DELETE_KEY = "delete"
        private const val EXTRA_REPORT_KEY = "report"
    }
}
