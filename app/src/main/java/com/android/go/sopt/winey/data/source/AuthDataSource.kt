package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetWineyFeed
import com.android.go.sopt.winey.data.service.AuthService
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val authService: AuthService
){
    suspend fun getWineyFeed(userId:Int, page:Int): ResponseGetWineyFeed =
        authService.getWineyFeedList(userId,page)
}