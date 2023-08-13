package com.android.go.sopt.winey.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.data.service.FeedService
import com.android.go.sopt.winey.data.source.FeedDataSource
import com.android.go.sopt.winey.data.source.pagingSource.MyFeedPagingSource
import com.android.go.sopt.winey.data.source.pagingSource.WineyFeedPagingSource
import com.android.go.sopt.winey.domain.entity.Like
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedDataSource: FeedDataSource,
    private val feedService: FeedService
) : FeedRepository {
    override suspend fun getWineyFeedList(): Flow<PagingData<WineyFeed>> =
        Pager(PagingConfig(FEED_PAGE_SIZE, prefetchDistance = LOAD_DISTANCE)) {
            WineyFeedPagingSource(feedService)
        }.flow

    override suspend fun getMyFeedList(): Flow<PagingData<WineyFeed>> =
        Pager(PagingConfig(FEED_PAGE_SIZE, prefetchDistance = LOAD_DISTANCE)) {
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

    companion object {
        const val FEED_PAGE_SIZE = 20
        const val LOAD_DISTANCE = 2
    }
}
