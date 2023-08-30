package com.go.sopt.winey.data.service

import com.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.go.sopt.winey.data.model.remote.request.RequestPatchNicknameDto
import com.go.sopt.winey.data.model.remote.response.ResponseCreateGoalDto
import com.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import com.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import com.go.sopt.winey.data.model.remote.response.ResponseLogoutDto
import com.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.go.sopt.winey.data.model.remote.response.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @GET("user")
    suspend fun getUser(): BaseResponse<ResponseGetUserDto?>

    @POST("goal")
    suspend fun postCreateGoal(
        @Body requestCreateGoalDto: RequestCreateGoalDto
    ): BaseResponse<ResponseCreateGoalDto>

    @POST("auth")
    suspend fun postLogin(
        @Header("Authorization") socialAccessToken: String,
        @Body requestLoginDto: RequestLoginDto
    ): BaseResponse<ResponseLoginDto>

    @POST("auth/token")
    suspend fun postReIssueToken(
        @Header("refreshToken") refreshToken: String
    ): BaseResponse<ResponseReIssueTokenDto>

    @POST("auth/sign-out")
    suspend fun postLogout(): ResponseLogoutDto

    @DELETE("auth/withdraw")
    suspend fun deleteUser(): BaseResponse<Unit>

    @GET("user/nickname/is-exist")
    suspend fun getNicknameDuplicateCheck(
        @Query("nickname") nickname: String
    ): BaseResponse<ResponseGetNicknameDuplicateCheckDto>

    @PATCH("user/nickname")
    suspend fun patchNickname(
        @Body requestPatchNicknameDto: RequestPatchNicknameDto
    ): BaseResponse<Unit>
}
