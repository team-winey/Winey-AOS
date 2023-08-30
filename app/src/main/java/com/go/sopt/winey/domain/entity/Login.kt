package com.go.sopt.winey.domain.entity

data class Login(
    val userId: Int,
    val accessToken: String,
    val refreshToken: String,
    val isRegistered: Boolean
)
