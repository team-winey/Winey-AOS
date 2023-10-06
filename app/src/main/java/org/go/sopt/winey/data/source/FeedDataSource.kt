package org.go.sopt.winey.data.source

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.go.sopt.winey.data.model.remote.request.RequestPostCommentDto
import org.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import org.go.sopt.winey.data.model.remote.response.ResponseDeleteCommentDto
import org.go.sopt.winey.data.model.remote.response.ResponseDeleteFeedDto
import org.go.sopt.winey.data.model.remote.response.ResponseGetFeedDetailDto
import org.go.sopt.winey.data.model.remote.response.ResponsePostCommentDto
import org.go.sopt.winey.data.model.remote.response.ResponsePostLikeDto
import org.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import org.go.sopt.winey.data.model.remote.response.base.BaseResponse
import org.go.sopt.winey.data.service.FeedService
import javax.inject.Inject

class FeedDataSource @Inject constructor(
    private val feedService: FeedService
) {
    suspend fun postFeedLike(
        feedId: Int,
        requestPostLikeDto: RequestPostLikeDto
    ): ResponsePostLikeDto =
        feedService.postFeedLike(feedId, requestPostLikeDto)

    suspend fun deleteFeed(feedId: Int): BaseResponse<ResponseDeleteFeedDto> =
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

    suspend fun deleteComment(commentId: Long): BaseResponse<ResponseDeleteCommentDto> =
        feedService.deleteComment(commentId)
}
