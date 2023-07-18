package com.android.go.sopt.winey.data.repository

import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.source.AuthDataSource
import com.android.go.sopt.winey.domain.entity.Like
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override suspend fun getWineyFeed(page: Int): Result<List<WineyFeed>> =
        runCatching {
            val response = authDataSource.getWineyFeed(page)
            response.toWineyFeed()
        }
    override suspend fun getUser(): Result<User> =
        runCatching {
            authDataSource.getUser().convertToUser()
        }
    override suspend fun postFeedLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto): Result<Like> =
        runCatching {
            authDataSource.postFeedLike(feedId,requestPostLikeDto).toLike()
        }
}