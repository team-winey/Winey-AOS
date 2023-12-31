package org.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.go.sopt.winey.domain.entity.Goal

@Serializable
data class ResponseCreateGoalDto(
    @SerialName("userId")
    val userId: Int,
    @SerialName("targetMoney")
    val targetMoney: Long,
    @SerialName("targetDate")
    val targetDate: String,
    @SerialName("createdAt")
    val createdAt: String
) {
    fun toGoal(): Goal {
        val data = this

        return Goal(
            userId = data.userId,
            targetMoney = data.targetMoney
        )
    }
}
