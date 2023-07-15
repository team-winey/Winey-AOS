package com.android.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetUserDto(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val data: Data?,
    @SerialName("message")
    val message: String
) {
    @Serializable
    data class Data(
        @SerialName("userResponseGoalDto")
        val userResponseGoalDto: UserResponseGoalDto?,
        @SerialName("userResponseUserDto")
        val userResponseUserDto: UserResponseUserDto?
    ) {
        @Serializable
        data class UserResponseGoalDto(
            @SerialName("duringGoalAmount")
            val duringGoalAmount: Int,
            @SerialName("duringGoalCount")
            val duringGoalCount: Int,
            @SerialName("isAttained")
            val isAttained: Boolean,
            @SerialName("isOver")
            val isOver: Boolean,
            @SerialName("targetDay")
            val targetDay: Int,
            @SerialName("targetMoney")
            val targetMoney: Int
        )

        @Serializable
        data class UserResponseUserDto(
            @SerialName("nickname")
            val nickname: String,
            @SerialName("userId")
            val userId: Int,
            @SerialName("userLevel")
            val userLevel: String
        )
    }
}