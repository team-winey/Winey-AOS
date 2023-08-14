package com.android.go.sopt.winey.data.repository

import com.android.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.android.go.sopt.winey.data.source.AuthDataSource
import com.android.go.sopt.winey.domain.entity.Goal
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override suspend fun getUser(): Result<User?> =
        runCatching {
            authDataSource.getUser().data?.toUser()
        }

    override suspend fun postCreateGoal(requestCreateGoalDto: RequestCreateGoalDto): Result<Goal?> =
        runCatching {
            authDataSource.postCreateGoal(requestCreateGoalDto).data?.toGoal()
        }

    override suspend fun postLogin(
        socialAccessToken: String,
        requestLoginDto: RequestLoginDto
    ): Result<ResponseLoginDto?> =
        runCatching {
            authDataSource.postLogin(socialAccessToken, requestLoginDto).data
        }

    override suspend fun postReIssueToken(refreshToken: String): Result<ResponseReIssueTokenDto?> =
        runCatching {
            authDataSource.postReIssueToken(refreshToken).data
        }

    override suspend fun getNicknameDuplicateCheck(nickname: String): Result<ResponseGetNicknameDuplicateCheckDto?> =
        runCatching {
            authDataSource.getNicknameDuplicateCheck(nickname).data
        }
}
