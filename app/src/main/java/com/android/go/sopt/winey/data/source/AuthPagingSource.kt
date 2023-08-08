package com.android.go.sopt.winey.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.go.sopt.winey.data.service.AuthService
import com.android.go.sopt.winey.domain.entity.WineyFeed
import kotlinx.coroutines.delay
import java.io.IOException

const val STARTING_KEY = 1
private const val DELAY_MILLIS = 1_000L

class AuthPagingSource(
    private val authService: AuthService
) : PagingSource<Int, WineyFeed>() {

    override fun getRefreshKey(state: PagingState<Int, WineyFeed>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WineyFeed> {
        val position = params.key ?: STARTING_KEY
        if (position != STARTING_KEY) delay(DELAY_MILLIS)
        return try {
            val feedData = authService.getWineyFeedList(position).toWineyFeed()
            LoadResult.Page(
                data = feedData,
                prevKey = if (position == STARTING_KEY) null else position - 1,
                nextKey = if (feedData.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            return PagingSource.LoadResult.Error(e)
        }
    }

}
