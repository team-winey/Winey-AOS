package com.android.go.sopt.winey.domain.entity

data class MypageModel(
    val nickname: String,
    val userLevel: String,
    val duringGoalAmount: String,
    val duringGoalCount: String,
    val targetMoney: String,
    val targetDay: String,
    val isOver: Boolean,
    val isAttained: Boolean
)
