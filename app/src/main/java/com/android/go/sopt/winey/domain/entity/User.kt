package com.android.go.sopt.winey.domain.entity

data class User(
    val nickname: String,
    val userLevel: String,
    val duringGoalAmount: Int,
    val duringGoalCount: Int,
    val targetMoney: Int,
    val targetDay: Int,
    val dday: Int,
    val isOver: Boolean,
    val isAttained: Boolean
)
