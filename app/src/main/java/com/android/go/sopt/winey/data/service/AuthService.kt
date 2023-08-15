package com.android.go.sopt.winey.data.service

import com.android.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLogoutDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.android.go.sopt.winey.data.model.remote.response.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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

    @GET("user/nickname/is-exist")
    suspend fun getNicknameDuplicateCheck(
        @Query("nickname") nickname: String
    ): BaseResponse<ResponseGetNicknameDuplicateCheckDto>
}
