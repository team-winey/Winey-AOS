package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.model.remote.request.RequestPostCommentDto
import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetFeedDetailDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostCommentDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.data.model.remote.response.base.BaseResponse
import com.android.go.sopt.winey.data.service.FeedService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class FeedDataSource @Inject constructor(
    private val feedService: FeedService
) {
    suspend fun postFeedLike(
        feedId: Int,
        requestPostLikeDto: RequestPostLikeDto
    ): ResponsePostLikeDto =
        feedService.postFeedLike(feedId, requestPostLikeDto)

    suspend fun deleteFeed(feedId: Int): BaseResponse<Unit> =
        feedService.deleteFeed(feedId)

    suspend fun postWineyFeedList(
        file: MultipartBody.Part?,
        requestMap: HashMap<String, RequestBody>
    ): BaseResponse<ResponsePostWineyFeedDto> =
        feedService.postWineyFeed(file, requestMap)

    suspend fun getFeedDetail(
        feedId: Int
    ): BaseResponse<ResponseGetFeedDetailDto> =
        feedService.getFeedDetail(feedId)

    suspend fun postComment(
        feedId: Int,
        requestPostCommentDto: RequestPostCommentDto
    ): BaseResponse<ResponsePostCommentDto> =
        feedService.postComment(feedId, requestPostCommentDto)
}
