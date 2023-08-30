package com.go.sopt.winey.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.go.sopt.winey.data.service.FeedService
import com.go.sopt.winey.domain.entity.WineyFeed
import kotlinx.coroutines.delay
import java.io.IOException

abstract class BasePagingSource(
    protected val feedService: FeedService
) : PagingSource<Int, WineyFeed>() {

    override fun getRefreshKey(state: PagingState<Int, WineyFeed>): Int? {
        return state.anchorPosition?.let { position ->
            val anchorPage = state.closestPageToPosition(position)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    abstract suspend fun getFeedList(position: Int): List<WineyFeed>

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WineyFeed> {
        val position = params.key ?: STARTING_KEY
        if (position != STARTING_KEY) delay(DELAY_MILLIS)

        return try {
            val feedData = getFeedList(position)
            LoadResult.Page(
                data = feedData,
                prevKey = if (position == STARTING_KEY) null else position - 1,
                nextKey = if (feedData.isEmpty() || feedData.first().isEnd) null else position + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        private const val STARTING_KEY = 1
        private const val DELAY_MILLIS = 1_000L
    }
}
