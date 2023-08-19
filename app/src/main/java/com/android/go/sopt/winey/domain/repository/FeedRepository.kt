package com.android.go.sopt.winey.domain.repository

import androidx.paging.PagingData
import com.android.go.sopt.winey.data.model.remote.request.RequestPostCommentDto
import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostCommentDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.domain.entity.DetailFeed
import com.android.go.sopt.winey.domain.entity.Like
import com.android.go.sopt.winey.domain.entity.WineyFeed
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface FeedRepository {
    suspend fun getWineyFeedList(): Flow<PagingData<WineyFeed>>

    suspend fun getMyFeedList(): Flow<PagingData<WineyFeed>>

    suspend fun postFeedLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto): Result<Like>

    suspend fun deleteFeed(feedId: Int): Result<Unit>

    suspend fun postWineyFeed(
        file: MultipartBody.Part?,
        requestMap: HashMap<String, RequestBody>
    ): Result<ResponsePostWineyFeedDto?>

    suspend fun getFeedDetail(feedId: Int): Result<DetailFeed?>

    suspend fun postComment(
        feedId: Long,
        requestPostCommentDto: RequestPostCommentDto
    ): Result<ResponsePostCommentDto?>
}
