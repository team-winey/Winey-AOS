package com.go.sopt.winey.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.go.sopt.winey.data.model.remote.request.RequestPostCommentDto
import com.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.go.sopt.winey.data.model.remote.response.ResponseDeleteCommentDto
import com.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.go.sopt.winey.data.service.FeedService
import com.go.sopt.winey.data.source.FeedDataSource
import com.go.sopt.winey.data.source.paging.MyFeedPagingSource
import com.go.sopt.winey.data.source.paging.WineyFeedPagingSource
import com.go.sopt.winey.domain.entity.Comment
import com.go.sopt.winey.domain.entity.DetailFeed
import com.go.sopt.winey.domain.entity.Like
import com.go.sopt.winey.domain.entity.WineyFeed
import com.go.sopt.winey.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedDataSource: FeedDataSource,
    private val feedService: FeedService
) : FeedRepository {
    override suspend fun getWineyFeedList(): Flow<PagingData<WineyFeed>> =
        Pager(PagingConfig(WINEYFEED_PAGE_SIZE, prefetchDistance = LOAD_DISTANCE)) {
            WineyFeedPagingSource(feedService)
        }.flow

    override suspend fun getMyFeedList(): Flow<PagingData<WineyFeed>> =
        Pager(PagingConfig(MYFEED_PAGE_SIZE, prefetchDistance = LOAD_DISTANCE)) {
            MyFeedPagingSource(feedService)
        }.flow

    override suspend fun postWineyFeed(
        file: MultipartBody.Part?,
        requestMap: HashMap<String, RequestBody>
    ): Result<ResponsePostWineyFeedDto?> =
        runCatching {
            feedDataSource.postWineyFeedList(file, requestMap).data
        }

    override suspend fun deleteFeed(feedId: Int): Result<Unit> =
        runCatching {
            feedDataSource.deleteFeed(feedId)
        }

    override suspend fun postFeedLike(
        feedId: Int,
        requestPostLikeDto: RequestPostLikeDto
    ): Result<Like> =
        runCatching {
            feedDataSource.postFeedLike(feedId, requestPostLikeDto).toLike()
        }

    override suspend fun getFeedDetail(feedId: Int): Result<DetailFeed?> =
        runCatching {
            feedDataSource.getFeedDetail(feedId).data?.toDetailFeed()
        }

    override suspend fun postComment(
        feedId: Int,
        requestPostCommentDto: RequestPostCommentDto
    ): Result<Comment?> =
        runCatching {
            feedDataSource.postComment(feedId, requestPostCommentDto).data?.toComment()
        }

    override suspend fun deleteComment(commentId: Long): Result<ResponseDeleteCommentDto?> =
        runCatching {
            feedDataSource.deleteComment(commentId).data
        }

    companion object {
        const val WINEYFEED_PAGE_SIZE = 20
        const val MYFEED_PAGE_SIZE = 10
        const val LOAD_DISTANCE = 2
    }
}
