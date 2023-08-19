package com.android.go.sopt.winey.presentation.main.notification

import android.os.Bundle
import androidx.activity.viewModels
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ActivityNotificationBinding
import com.android.go.sopt.winey.util.binding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BindingActivity<ActivityNotificationBinding>(R.layout.activity_notification) {
    private lateinit var notificationAdapter: NotificationAdapter
    private val viewModel: NotificationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNotificationAdapter()
    }

    private fun initNotificationAdapter() {
        notificationAdapter = NotificationAdapter()
    }
}