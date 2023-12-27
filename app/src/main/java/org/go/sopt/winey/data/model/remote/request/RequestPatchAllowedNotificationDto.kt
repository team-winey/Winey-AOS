package org.go.sopt.winey.data.model.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPatchAllowedNotificationDto(
    @SerialName("allowedPush")
    val allowedPush: Boolean
)
