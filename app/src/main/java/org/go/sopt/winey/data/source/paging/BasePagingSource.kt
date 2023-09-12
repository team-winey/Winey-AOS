package org.go.sopt.winey.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.go.sopt.winey.data.service.FeedService
import org.go.sopt.winey.domain.entity.WineyFeed
import java.io.IOException

abstract class BasePagingSource(
    protected val feedService: FeedService
) : PagingSource<Int, WineyFeed>() {
    abstract suspend fun getFeedList(pageNumber: Int): List<WineyFeed>

    override fun getRefreshKey(state: PagingState<Int, WineyFeed>): Int? {
        return state.anchorPosition?.let { position ->
            val anchorPage = state.closestPageToPosition(position)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WineyFeed> {
        return try {
            val pageNumber = params.key ?: FIRST_PAGE_NUM
            val feeds = getFeedList(pageNumber)

            LoadResult.Page(
                data = feeds,
                prevKey = if (pageNumber == FIRST_PAGE_NUM) null else pageNumber - 1,
                nextKey = if (feeds.isEmpty() || feeds.first().isEnd) null else pageNumber + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        private const val FIRST_PAGE_NUM = 1
        private const val DELAY_MILLIS = 1_000L
    }
}
