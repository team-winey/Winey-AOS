package com.android.go.sopt.winey.presentation.main.notification

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityNotificationBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import com.android.go.sopt.winey.util.context.snackBar
import com.android.go.sopt.winey.util.view.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
        setupGetNotificationStateObserver()
    }

    private fun initNotificationAdapter() {
        notificationAdapter = NotificationAdapter()
        binding.rvNotificationPost.adapter = notificationAdapter
    }

    private fun setupGetNotificationStateObserver() {
        viewModel.getNotificationState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    notificationAdapter.submitList(state.data)
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
}
