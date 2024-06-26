package org.go.sopt.winey.presentation.main.notification

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ActivityNotificationBinding
import org.go.sopt.winey.presentation.main.MainActivity
import org.go.sopt.winey.presentation.main.feed.detail.DetailActivity
import org.go.sopt.winey.presentation.main.mypage.goal.GoalPathActivity
import org.go.sopt.winey.util.binding.BindingActivity
import org.go.sopt.winey.util.context.snackBar
import org.go.sopt.winey.util.state.UiState

@AndroidEntryPoint
class NotificationActivity :
    BindingActivity<ActivityNotificationBinding>(R.layout.activity_notification) {
    private val viewModel by viewModels<NotificationViewModel>()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNotificationAdapter()
        viewModel.getNotification()
        initBackButtonClickListener()
        initSwipeRefreshListener()
        setupGetNotificationStateObserver()
    }

    private fun initNotificationAdapter() {
        notificationAdapter = NotificationAdapter(
            navigateFeedDetail = { feedId -> navigateToDetail(feedId) },
            navigateGoalPath = { navigateToGoalPath() },
            navigateMyPage = { navigateToMyPage() }
        )
        binding.rvNotificationPost.adapter = notificationAdapter
    }

    private fun setupGetNotificationStateObserver() {
        viewModel.getNotificationState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    notificationAdapter.setData(state.data)
                }

                is UiState.Failure -> {
                    snackBar(binding.root) { state.msg }
                }

                is UiState.Empty -> {
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun initBackButtonClickListener() {
        binding.ivNotificationBack.setOnClickListener {
            finish()
        }
    }

    private fun initSwipeRefreshListener() {
        binding.layoutNotificationRefresh.setOnRefreshListener {
            viewModel.getNotification()
            binding.layoutNotificationRefresh.isRefreshing = false
        }
    }

    private fun navigateToDetail(feedId: Int?) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(MainActivity.KEY_FEED_ID, feedId)
        startActivity(intent)
    }

    private fun navigateToGoalPath() {
        val intent = Intent(this, GoalPathActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMyPage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.KEY_FROM_NOTI, true)
        startActivity(intent)
    }
}
