package org.go.sopt.winey.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.go.sopt.winey.data.model.remote.request.RequestPostCommentDto
import org.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import org.go.sopt.winey.data.model.remote.response.ResponseDeleteCommentDto
import org.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import org.go.sopt.winey.domain.entity.Comment
import org.go.sopt.winey.domain.entity.DetailFeed
import org.go.sopt.winey.domain.entity.Like
import org.go.sopt.winey.domain.entity.WineyFeed

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
        feedId: Int,
        requestPostCommentDto: RequestPostCommentDto
    ): Result<Comment?>

    suspend fun deleteComment(commentId: Long): Result<ResponseDeleteCommentDto?>
}
