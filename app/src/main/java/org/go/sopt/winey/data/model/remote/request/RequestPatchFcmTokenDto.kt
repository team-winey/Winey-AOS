package org.go.sopt.winey.data.model.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPatchFcmTokenDto(
    @SerialName("token")
    val fcmToken: String
)
