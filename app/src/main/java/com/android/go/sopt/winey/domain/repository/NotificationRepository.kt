package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.data.model.remote.response.ResponseHasNewNotificationDto
import com.android.go.sopt.winey.domain.entity.Notification

interface NotificationRepository {
    suspend fun getNotification(): Result<List<Notification>?>

    suspend fun getHasNewNotification(): Result<ResponseHasNewNotificationDto?>

    suspend fun patchCheckAllNotification(): Result<Unit>
}
