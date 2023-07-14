package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.data.service.AuthService
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val authService: AuthService,
) {
    suspend fun getUser(
        userId:Int
    ): ResponseGetUserDto = authService.getUser(userId)
}