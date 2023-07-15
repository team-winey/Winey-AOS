package com.android.go.sopt.winey.data.repository

import com.android.go.sopt.winey.data.source.AuthDataSource
import com.android.go.sopt.winey.domain.entity.WineyFeedModel
import com.android.go.sopt.winey.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthDataSource
) : AuthRepository {
    override suspend fun getWineyFeed(page: Int): Result<List<WineyFeedModel>> =
        runCatching {
            val response = dataSource.getWineyFeed(page)
            response.convertToWineyFeedModel()
        }
}