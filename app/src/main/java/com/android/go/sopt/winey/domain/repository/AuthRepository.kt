package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.domain.entity.Like
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.entity.WineyFeed

interface AuthRepository {
    suspend fun getUser(): Result<User>
    suspend fun getWineyFeed(page: Int): Result<List<WineyFeed>>
    suspend fun postFeedLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto): Result<Like>
}