package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.service.AuthService
import com.android.go.sopt.winey.domain.entity.WineyFeed
import javax.inject.Inject

class MyFeedPagingSource @Inject constructor(
    authService: AuthService
) : BasePagingSource(authService) {
    override suspend fun getFeedList(position: Int): List<WineyFeed> {
        return authService.getMyFeedList(position).toWineyFeed()
    }
}
