package com.go.sopt.winey.data.source.paging

import com.go.sopt.winey.data.service.FeedService
import com.go.sopt.winey.domain.entity.WineyFeed
import javax.inject.Inject

class WineyFeedPagingSource @Inject constructor(
    feedService: FeedService
) : BasePagingSource(feedService) {
    override suspend fun getFeedList(position: Int): List<WineyFeed> {
        return feedService.getWineyFeedList(position).toWineyFeed()
    }
}
