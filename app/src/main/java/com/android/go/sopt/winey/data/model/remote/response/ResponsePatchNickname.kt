package com.android.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePatchNickname(
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String
)
