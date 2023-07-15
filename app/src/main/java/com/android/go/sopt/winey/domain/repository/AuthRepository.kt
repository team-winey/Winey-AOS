package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto

interface AuthRepository {
    suspend fun getUser(): Result<ResponseGetUserDto>
}