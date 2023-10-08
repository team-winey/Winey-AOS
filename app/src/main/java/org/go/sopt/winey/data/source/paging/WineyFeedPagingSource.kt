package org.go.sopt.winey.data.source.paging

import org.go.sopt.winey.data.service.FeedService
import org.go.sopt.winey.domain.entity.WineyFeed
import javax.inject.Inject

class WineyFeedPagingSource @Inject constructor(
    feedService: FeedService
) : BasePagingSource(feedService) {
    override suspend fun getFeedList(pageNumber: Int): List<WineyFeed> {
        return feedService.getWineyFeedList(pageNumber).toWineyFeed()
    }
}
