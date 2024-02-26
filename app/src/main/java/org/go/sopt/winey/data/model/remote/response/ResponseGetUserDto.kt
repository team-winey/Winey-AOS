package org.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.go.sopt.winey.domain.entity.UserV2

@Serializable
data class ResponseGetUserDto(
    @SerialName("userData")
    val userData: UserData
) {
    @Serializable
    data class UserData(
        @SerialName("userId")
        val userId: Int,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("userLevel")
        val userLevel: String,
        @SerialName("fcmIsAllowed")
        val fcmIsAllowed: Boolean,
        @SerialName("accumulatedAmount")
        val accumulatedAmount: Int,
        @SerialName("accumulatedCount")
        val accumulatedCount: Int,
        @SerialName("amountSavedHundredDays")
        val amountSavedHundredDays: Int,
        @SerialName("amountSavedTwoWeeks")
        val amountSavedTwoWeeks: Int,
        @SerialName("amountSpentTwoWeeks")
        val amountSpentTwoWeeks: Int,
        @SerialName("remainingAmount")
        val remainingAmount: Int,
        @SerialName("remainingCount")
        val remainingCount: Int
    )

    fun toUser(): UserV2 {
        return UserV2(
            nickname = userData.nickname,
            userLevel = userData.userLevel,
            fcmIsAllowed = userData.fcmIsAllowed,
            accumulatedAmount = userData.accumulatedAmount,
            accumulatedCount = userData.accumulatedCount,
            amountSavedHundredDays = userData.amountSavedHundredDays,
            amountSavedTwoWeeks = userData.amountSavedTwoWeeks,
            amountSpentTwoWeeks = userData.amountSpentTwoWeeks,
            remainingAmount = userData.remainingAmount,
            remainingCount = userData.remainingCount
        )
    }
}
