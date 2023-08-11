package com.android.go.sopt.winey.presentation.main.feed

import com.android.go.sopt.winey.util.view.view.FeedUiState

sealed class FeedMultiViewItem(
    private val viewType: FeedUiState
) {
    object Loading : FeedMultiViewItem(FeedUiState.Loading)

    data class WineyFeed(
        val feedId: Int,
        val feedImage: String,
        val feedMoney: Long,
        val feedTitle: String,
        var isLiked: Boolean,
        var likes: Int,
        val nickName: String,
        val userId: Int,
        val writerLevel: Int,
        val totalPageSize: Int,
        val isEnd: Boolean
    ) : FeedMultiViewItem(FeedUiState.Success)

    fun getViewType() = viewType
}
