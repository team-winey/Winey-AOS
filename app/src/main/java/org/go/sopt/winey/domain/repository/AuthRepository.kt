package org.go.sopt.winey.domain.repository

import org.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import org.go.sopt.winey.data.model.remote.request.RequestLoginDto
import org.go.sopt.winey.data.model.remote.request.RequestPatchNicknameDto
import org.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import org.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import org.go.sopt.winey.data.model.remote.response.ResponseLogoutDto
import org.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import org.go.sopt.winey.domain.entity.Goal
import org.go.sopt.winey.domain.entity.User

interface AuthRepository {
    suspend fun getUser(): Result<User?>

    suspend fun postCreateGoal(requestCreateGoalDto: RequestCreateGoalDto): Result<Goal?>

    suspend fun postLogin(
        socialAccessToken: String,
        requestLoginDto: RequestLoginDto
    ): Result<ResponseLoginDto?>

    suspend fun postReIssueToken(refreshToken: String): Result<ResponseReIssueTokenDto?>

    suspend fun postLogout(): Result<ResponseLogoutDto>

    suspend fun deleteUser(): Result<Unit>

    suspend fun getNicknameDuplicateCheck(nickname: String): Result<ResponseGetNicknameDuplicateCheckDto?>

    suspend fun patchNickname(requestPatchNicknameDto: RequestPatchNicknameDto): Result<Unit>
}
