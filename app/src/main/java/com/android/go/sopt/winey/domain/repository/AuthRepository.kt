package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.domain.entity.User

interface AuthRepository {
    suspend fun getUser(): Result<User>
}