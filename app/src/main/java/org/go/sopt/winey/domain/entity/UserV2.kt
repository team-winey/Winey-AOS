package org.go.sopt.winey.domain.entity

data class UserV2(
    val nickname: String,
    val userLevel: String,
    val fcmIsAllowed: Boolean,
    val accumulatedAmount: Int,
    val accumulatedCount: Int,
    val amountSavedHundredDays: Int,
    val amountSavedTwoWeeks: Int,
    val amountSpentTwoWeeks: Int,
    val remainingAmount: Int,
    val remainingCount: Int,
    val isLevelUpAmountConditionMet: Boolean,
    val isLevelUpCountConditionMet: Boolean
)
