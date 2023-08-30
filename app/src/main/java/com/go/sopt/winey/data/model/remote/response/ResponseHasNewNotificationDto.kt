package com.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseHasNewNotificationDto(
    @SerialName("hasNewNotification")
    val hasNewNotification: Boolean
)
