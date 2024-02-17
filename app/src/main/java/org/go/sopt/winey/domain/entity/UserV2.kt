package org.go.sopt.winey.domain.entity

import kotlinx.serialization.SerialName

data class UserV2 (
    val nickname: String,
    val userLevel: String,
    val fcmIsAllowed: Boolean,
    val accumulatedAmount: Int,
    val amountSavedHundredDays: Int,
    val amountSavedTwoWeeks: Int,
    val amountSpentTwoWeeks: Int
)
