package com.android.go.sopt.winey.domain.entity

data class User(
    val nickname: String = "",
    val userLevel: String = "",
    val duringGoalAmount: Long = 0,
    val duringGoalCount: Long = 0,
    val targetMoney: Int = 0,
    val targetDay: Int = 0,
    val dday: Int = 0,
    val isOver: Boolean = false,
    val isAttained: Boolean = false
)
