package org.go.sopt.winey.data.source

import org.go.sopt.winey.data.model.remote.response.ResponseGetNotificationDto
import org.go.sopt.winey.data.model.remote.response.ResponseHasNewNotificationDto
import org.go.sopt.winey.data.model.remote.response.base.BaseResponse
import org.go.sopt.winey.data.service.NotificationService
import javax.inject.Inject

class NotificationDataSource @Inject constructor(
    private val notificationService: NotificationService
) {
    suspend fun getNotification(): BaseResponse<ResponseGetNotificationDto> = notificationService.getNotification()

    suspend fun getHasNewNotification(): BaseResponse<ResponseHasNewNotificationDto> = notificationService.getHasNewNotification()

    suspend fun patchCheckAllNotification(): BaseResponse<Unit> = notificationService.patchCheckAllNotification()
}
