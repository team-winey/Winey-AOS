package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.entity.WineyFeed

interface AuthRepository {
    suspend fun getUser(): Result<User>
    suspend fun getWineyFeed(page: Int): Result<List<WineyFeed>>
    suspend fun getMyFeedList(page: Int): Result<List<WineyFeed>>
}