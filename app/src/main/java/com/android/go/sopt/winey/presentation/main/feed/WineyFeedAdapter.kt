package com.android.go.sopt.winey.presentation.main.feed

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.util.view.ItemDiffCallback
import com.android.go.sopt.winey.util.view.view.FeedUiState

class WineyFeedAdapter(
    private val likeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit,
    private val showPopupMenu: (View, FeedMultiViewItem.WineyFeed) -> Unit
) :
    PagingDataAdapter<FeedMultiViewItem, FeedMultiViewHolder<FeedMultiViewItem>>(diffUtil) {

    private val multiViewHolderFactory = FeedMultiViewHolderFactory(likeButtonClick, showPopupMenu)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedMultiViewHolder<FeedMultiViewItem> {
        return multiViewHolderFactory.getViewHolder(parent, FeedUiState.values()[viewType])
    }

    override fun onBindViewHolder(holder: FeedMultiViewHolder<FeedMultiViewItem>, position: Int) {
        getItem(position)?.let { item ->
            when (item) {
                is FeedMultiViewItem.WineyFeed -> (holder as FeedMultiViewHolder.FeedViewHolder).bind(item)
                is FeedMultiViewItem.Loading -> (holder as FeedMultiViewHolder.LoadingViewHolder).bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = snapshot().items[position]
        return item.getViewType().ordinal
    }

    fun updateLikeStatus(feedId: Int, isLiked: Boolean) {
        val data = snapshot().items
        val index = data.indexOfFirst { (it as? FeedMultiViewItem.WineyFeed)?.feedId == feedId }
        if (index != -1) {
            val currentItem = data[index] as? FeedMultiViewItem.WineyFeed
            currentItem?.let { item ->
                item.isLiked = isLiked
                if (isLiked) {
                    item.likes++
                } else {
                    item.likes--
                }
                notifyItemChanged(index)
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<FeedMultiViewItem>(
            onItemsTheSame = { old, new -> (old as? FeedMultiViewItem.WineyFeed)?.isLiked == (new as? FeedMultiViewItem.WineyFeed)?.isLiked },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
