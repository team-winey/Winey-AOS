package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.domain.entity.Like
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.entity.WineyFeed
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthRepository {
    suspend fun getUser(): Result<User>

    suspend fun getWineyFeedList(page: Int): Result<List<WineyFeed>>

    suspend fun getMyFeedList(page: Int): Result<List<WineyFeed>>

    suspend fun postFeedLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto): Result<Like>

    suspend fun postWineyFeed(
        file: MultipartBody.Part?,
        requestMap: HashMap<String, RequestBody>
    ): Result<ResponsePostWineyFeedDto?>
}