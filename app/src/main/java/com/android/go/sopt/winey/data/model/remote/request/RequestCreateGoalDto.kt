package com.android.go.sopt.winey.data.model.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestCreateGoalDto(
    @SerialName("targetMoney")
    val targetMoney: Int,
    @SerialName("targetDay")
    val targetDay: Int
)
