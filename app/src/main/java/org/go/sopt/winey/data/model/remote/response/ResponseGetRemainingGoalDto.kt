package org.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.go.sopt.winey.domain.entity.RemainingGoal

@Serializable
data class ResponseGetRemainingGoalDto(
    @SerialName("userLevel")
    val userLevel: String,
    @SerialName("remainingAmount")
    val remainingAmount: Int,
    @SerialName("remainingCount")
    val remainingCount: Int
) {
    fun toRemainingGoal() = RemainingGoal(
        userLevel = userLevel,
        remainingMoney = remainingAmount,
        remainingFeed = remainingCount
    )
}
