package com.android.go.sopt.winey.data.repository

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.data.source.AuthDataSource
import com.android.go.sopt.winey.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun getUser(userId: Int): Result<ResponseGetUserDto> =
        runCatching {
            authDataSource.getUser(userId)
        }
}