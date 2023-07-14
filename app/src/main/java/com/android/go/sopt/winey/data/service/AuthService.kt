package com.android.go.sopt.winey.data.service

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Header

interface AuthService {
    @GET("user")
    suspend fun getUser(
        @Header("userId") userId: Int
    ): ResponseGetUserDto
}