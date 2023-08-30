package com.go.sopt.winey.data.model.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPatchNicknameDto(
    @SerialName("nickname")
    val nickname: String
)
