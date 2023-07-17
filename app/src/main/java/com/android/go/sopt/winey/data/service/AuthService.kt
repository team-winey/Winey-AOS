package com.android.go.sopt.winey.data.service

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetWineyFeedDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthService {
    @GET("/user")
    suspend fun getUser(): ResponseGetUserDto

    @GET("feed")
    suspend fun getWineyFeedList(
        @Query("page") page:Int
    ): ResponseGetWineyFeedDto
}