package org.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePatchAllowedNotificationDto(
    @SerialName("isAllowed")
    val isAllowed: Boolean
)
