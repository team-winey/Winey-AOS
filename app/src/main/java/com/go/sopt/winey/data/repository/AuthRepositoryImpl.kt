package com.go.sopt.winey.data.repository

import com.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.go.sopt.winey.data.model.remote.request.RequestPatchNicknameDto
import com.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import com.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import com.go.sopt.winey.data.model.remote.response.ResponseLogoutDto
import com.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.go.sopt.winey.data.source.AuthDataSource
import com.go.sopt.winey.domain.entity.Goal
import com.go.sopt.winey.domain.entity.User
import com.go.sopt.winey.domain.repository.AuthRepository
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

    override suspend fun postLogout(): Result<ResponseLogoutDto> =
        runCatching {
            authDataSource.postLogout()
        }

    override suspend fun deleteUser(): Result<Unit> =
        runCatching {
            authDataSource.deleteUser()
        }

    override suspend fun getNicknameDuplicateCheck(nickname: String): Result<ResponseGetNicknameDuplicateCheckDto?> =
        runCatching {
            authDataSource.getNicknameDuplicateCheck(nickname).data
        }

    override suspend fun patchNickname(
        requestPatchNicknameDto: RequestPatchNicknameDto
    ): Result<Unit> =
        runCatching {
            authDataSource.patchNickname(requestPatchNicknameDto).data
        }
}