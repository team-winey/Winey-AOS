package org.go.sopt.winey.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityMainBinding
import org.go.sopt.winey.presentation.main.feed.WineyFeedFragment
import org.go.sopt.winey.presentation.main.mypage.MyPageFragment
import org.go.sopt.winey.presentation.main.recommend.RecommendFragment
import org.go.sopt.winey.presentation.onboarding.login.LoginActivity
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.snackBar
import org.go.sopt.winey.util.context.stringOf
import org.go.sopt.winey.util.context.wineySnackbar
import org.go.sopt.winey.util.view.UiState

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel by viewModels<MainViewModel>()
    private val isUploadSuccess by lazy { intent.extras?.getBoolean(KEY_FEED_UPLOAD, false) }
    private val isDeleteSuccess by lazy { intent.extras?.getBoolean(KEY_FEED_DELETE, false) }
    private val prevScreenName by lazy { intent.extras?.getString(KEY_PREV_SCREEN_NAME, "") }

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
        if (intent.getBooleanExtra(KEY_TO_MYPAGE, false)) {
            navigateToMyPageWithBundle(KEY_FROM_NOTI, true)
        } else {
            if (prevScreenName == VAL_MY_FEED_SCREEN) {
                navigateToMyPageWithBundle(KEY_TO_MYFEED, true)
            } else {
                navigateTo<WineyFeedFragment>()
            }
        }
    }

    private fun showSuccessSnackBar() {
        if (isUploadSuccess == true) {
            wineySnackbar(binding.root, true, stringOf(R.string.snackbar_upload_success))
        }

        if (isDeleteSuccess == true) {
            wineySnackbar(binding.root, true, stringOf(R.string.snackbar_feed_delete_success))
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

    private fun navigateToMyPageWithBundle(key: String, value: Boolean) {
        supportFragmentManager.commit {
            val bundle = Bundle()
            bundle.putBoolean(key, value)
            val myPageFragment = MyPageFragment()
            myPageFragment.arguments = bundle
            replace(R.id.fcv_main, myPageFragment)
            binding.bnvMain.selectedItemId = R.id.menu_mypage
        }
    }

    companion object {
        private const val KEY_FEED_UPLOAD = "upload"
        private const val KEY_FEED_DELETE = "delete"
        private const val KEY_PREV_SCREEN_NAME = "PREV_SCREEN_NAME"
        private const val KEY_FROM_NOTI = "fromNoti"
        private const val KEY_TO_MYFEED = "toMyFeed"
        private const val KEY_TO_MYPAGE = "navigateMypage"
        private const val VAL_MY_FEED_SCREEN = "MyFeedFragment"
    }
}
