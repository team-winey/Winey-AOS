package org.go.sopt.winey.data.repository

import org.go.sopt.winey.data.model.remote.response.ResponseHasNewNotificationDto
import org.go.sopt.winey.data.source.NotificationDataSource
import org.go.sopt.winey.domain.entity.Notification
import org.go.sopt.winey.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDataSource: NotificationDataSource
) : NotificationRepository {
    override suspend fun getNotification(): Result<List<Notification>?> =
        runCatching {
            notificationDataSource.getNotification().data?.toNotification()
        }

    override suspend fun getHasNewNotification(): Result<ResponseHasNewNotificationDto?> =
        runCatching {
            notificationDataSource.getHasNewNotification().data
        }

    override suspend fun patchCheckAllNotification(): Result<Unit> =
        runCatching {
            notificationDataSource.patchCheckAllNotification().data
        }
}
