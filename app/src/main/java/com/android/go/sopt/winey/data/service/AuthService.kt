package com.android.go.sopt.winey.data.service

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import retrofit2.http.GET

interface AuthService {
    @GET("/user")
    suspend fun getUser(): ResponseGetUserDto
}