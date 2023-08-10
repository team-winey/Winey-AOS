package com.android.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetNicknameDuplicateCheckDto(
    @SerialName("isDuplicated")
    val isDuplicated: Boolean
)
