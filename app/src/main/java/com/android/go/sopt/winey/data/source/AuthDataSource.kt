package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.android.go.sopt.winey.data.model.remote.request.RequestPatchNicknameDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLogoutDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.android.go.sopt.winey.data.model.remote.response.base.BaseResponse
import com.android.go.sopt.winey.data.service.AuthService
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val authService: AuthService
) {
    suspend fun getUser(): BaseResponse<ResponseGetUserDto?> = authService.getUser()

    suspend fun postCreateGoal(requestCreateGoalDto: RequestCreateGoalDto): BaseResponse<ResponseCreateGoalDto> =
        authService.postCreateGoal(requestCreateGoalDto)

    suspend fun postLogin(
        socialAccessToken: String,
        requestLoginDto: RequestLoginDto
    ): BaseResponse<ResponseLoginDto> =
        authService.postLogin(socialAccessToken, requestLoginDto)

    suspend fun postReIssueToken(
        refreshToken: String
    ): BaseResponse<ResponseReIssueTokenDto> =
        authService.postReIssueToken(refreshToken)

    suspend fun postLogout(): ResponseLogoutDto = authService.postLogout()

    suspend fun getNicknameDuplicateCheck(nickname: String): BaseResponse<ResponseGetNicknameDuplicateCheckDto> =
        authService.getNicknameDuplicateCheck(nickname)

    suspend fun patchNickname(
        requestPatchNicknameDto: RequestPatchNicknameDto
    ): BaseResponse<Unit> =
        authService.patchNickname(requestPatchNicknameDto)
}
