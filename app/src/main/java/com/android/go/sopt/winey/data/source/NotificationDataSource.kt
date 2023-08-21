package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetNotificationDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseHasNewNotificationDto
import com.android.go.sopt.winey.data.model.remote.response.base.BaseResponse
import com.android.go.sopt.winey.data.service.NotificationService
import javax.inject.Inject

class NotificationDataSource @Inject constructor(
    private val notificationService: NotificationService
) {
    suspend fun getNotification(): BaseResponse<ResponseGetNotificationDto> = notificationService.getNotification()

    suspend fun getHasNewNotification(): BaseResponse<ResponseHasNewNotificationDto> = notificationService.getHasNewNotification()

    suspend fun patchCheckAllNotification(): BaseResponse<Unit> = notificationService.patchCheckAllNotification()
}
