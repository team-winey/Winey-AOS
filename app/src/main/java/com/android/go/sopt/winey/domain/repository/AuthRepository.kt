package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.android.go.sopt.winey.data.model.remote.request.RequestPatchNicknameDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLogoutDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.android.go.sopt.winey.domain.entity.Goal
import com.android.go.sopt.winey.domain.entity.User

interface AuthRepository {
    suspend fun getUser(): Result<User?>

    suspend fun postCreateGoal(requestCreateGoalDto: RequestCreateGoalDto): Result<Goal?>

    suspend fun postLogin(
        socialAccessToken: String,
        requestLoginDto: RequestLoginDto
    ): Result<ResponseLoginDto?>

    suspend fun postReIssueToken(refreshToken: String): Result<ResponseReIssueTokenDto?>

    suspend fun postLogout(): Result<ResponseLogoutDto>

    suspend fun getNicknameDuplicateCheck(nickname: String): Result<ResponseGetNicknameDuplicateCheckDto?>

    suspend fun patchNickname(requestPatchNicknameDto: RequestPatchNicknameDto): Result<Unit>
}
