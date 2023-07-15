package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.domain.entity.WineyFeedModel

interface AuthRepository {
    suspend fun getWineyFeed(userId: Int, page: Int): Result<List<WineyFeedModel>>
}