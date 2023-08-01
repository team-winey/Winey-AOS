package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetUserDto(
    @SerialName("userResponseGoalDto")
    val userResponseGoalDto: UserResponseGoalDto?,
    @SerialName("userResponseUserDto")
    val userResponseUserDto: UserResponseUserDto?
) {
    @Serializable
    data class UserResponseGoalDto(
        @SerialName("duringGoalAmount")
        val duringGoalAmount: Long,
        @SerialName("duringGoalCount")
        val duringGoalCount: Long,
        @SerialName("isAttained")
        val isAttained: Boolean,
        @SerialName("isOver")
        val isOver: Boolean,
        @SerialName("targetDay")
        val targetDay: Int,
        @SerialName("targetMoney")
        val targetMoney: Int,
        @SerialName("dday")
        val dday: Int
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

    fun toUser(): User {
        val data = this
        val userResponseUserDto = data.userResponseUserDto

        return User(
            nickname = userResponseUserDto?.nickname.orEmpty(),
            userLevel = userResponseUserDto?.userLevel.orEmpty(),
            duringGoalAmount = data.userResponseGoalDto?.duringGoalAmount ?: 0,
            duringGoalCount = data.userResponseGoalDto?.duringGoalCount ?: 0,
            targetMoney = data.userResponseGoalDto?.targetMoney ?: 0,
            targetDay = data.userResponseGoalDto?.targetDay ?: 0,
            dday = data.userResponseGoalDto?.dday ?: 0,
            isOver = data.userResponseGoalDto?.isOver ?: true,
            isAttained = data.userResponseGoalDto?.isAttained ?: false
        )
    }
}
