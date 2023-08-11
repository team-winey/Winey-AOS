package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.util.view.view.FeedUiState

class FeedMultiViewHolderFactory(
    private val onLikeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit,
    private val showPopupMenu: (View, FeedMultiViewItem.WineyFeed) -> Unit
) {
    fun getViewHolder(
        parent: ViewGroup,
        viewType: FeedUiState
    ): FeedMultiViewHolder<FeedMultiViewItem> {
        return when (viewType) {
            FeedUiState.Loading ->
                FeedMultiViewHolder.LoadingViewHolder(
                    viewBind(
                        parent,
                        R.layout.progress_bar_loading
                    )
                )

            FeedUiState.Success ->
                FeedMultiViewHolder.FeedViewHolder(
                    viewBind(parent, R.layout.item_wineyfeed_post),
                    onLikeButtonClick = onLikeButtonClick,
                    showPopupMenu = showPopupMenu
                )
        } as FeedMultiViewHolder<FeedMultiViewItem>
    }

    private fun <T : ViewDataBinding> viewBind(parent: ViewGroup, layoutRes: Int): T {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutRes,
            parent,
            false
        )
    }
}
