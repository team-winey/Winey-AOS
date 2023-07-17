package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.domain.entity.WineyFeed

interface AuthRepository {
    suspend fun getWineyFeed(page: Int): Result<List<WineyFeed>>
}