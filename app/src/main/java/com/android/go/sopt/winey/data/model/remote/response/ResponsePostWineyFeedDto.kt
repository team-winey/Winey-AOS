package com.android.go.sopt.winey.data.model.remote.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostWineyFeedDto(
    @SerialName("feedId")
    val feedId: Int,
    @SerialName("createdAt")
    val createdAt: LocalDateTime
)