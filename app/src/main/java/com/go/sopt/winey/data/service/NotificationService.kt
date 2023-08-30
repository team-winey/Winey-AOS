package com.go.sopt.winey.data.service

import com.go.sopt.winey.data.model.remote.response.ResponseGetNotificationDto
import com.go.sopt.winey.data.model.remote.response.ResponseHasNewNotificationDto
import com.go.sopt.winey.data.model.remote.response.base.BaseResponse
import retrofit2.http.GET
import retrofit2.http.PATCH

interface NotificationService {
    @GET("noti")
    suspend fun getNotification(): BaseResponse<ResponseGetNotificationDto>

    @GET("noti/check")
    suspend fun getHasNewNotification(): BaseResponse<ResponseHasNewNotificationDto>

    @PATCH("noti")
    suspend fun patchCheckAllNotification(): BaseResponse<Unit>
}
