package com.android.go.sopt.winey.domain.entity

data class User(
    val nickname: String,
    val userLevel: String,
    val duringGoalAmount: Long,
    val duringGoalCount: Long,
    val targetMoney: Int,
    val targetDay: Int,
    val dday: Int,
    val isOver: Boolean,
    val isAttained: Boolean
)
