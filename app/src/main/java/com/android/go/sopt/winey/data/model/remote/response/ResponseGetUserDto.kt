package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.MypageModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.DecimalFormat

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
    fun toMypageModel(response: ResponseGetUserDto): MypageModel {
        val data = response.data
        val userResponseUserDto = data?.userResponseUserDto

        return MypageModel(
            nickname = userResponseUserDto?.nickname.orEmpty(),
            userLevel = userResponseUserDto?.userLevel.orEmpty(),
            duringGoalAmount = DecimalFormat("#,###").format(data?.userResponseGoalDto?.duringGoalAmount?: 0),
            duringGoalCount = DecimalFormat("#,###").format(data?.userResponseGoalDto?.duringGoalCount ?: 0),
            targetMoney = DecimalFormat("#,###").format(data?.userResponseGoalDto?.targetMoney ?: 0),
            targetDay = DecimalFormat("#,###").format(data?.userResponseGoalDto?.targetDay ?: 0),
            isOver = data?.userResponseGoalDto?.isOver ?: false,
            isAttained = data?.userResponseGoalDto?.isAttained ?: false
        )
    }
}