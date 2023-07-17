package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetWineyFeedDto
import com.android.go.sopt.winey.data.service.AuthService
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val authService: AuthService
) {
    suspend fun getUser(): ResponseGetUserDto = authService.getUser()

    suspend fun getWineyFeed(page: Int): ResponseGetWineyFeedDto =
        authService.getWineyFeedList(page)
}