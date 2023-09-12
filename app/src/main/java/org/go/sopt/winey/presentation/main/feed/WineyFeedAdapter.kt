package org.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.databinding.ItemWineyfeedPostBinding
import org.go.sopt.winey.domain.entity.DetailFeed
import org.go.sopt.winey.domain.entity.WineyFeed
import org.go.sopt.winey.util.view.ItemDiffCallback
import org.go.sopt.winey.util.view.setOnSingleClickListener

class WineyFeedAdapter(
    private val onlikeButtonClicked: (WineyFeed) -> Unit,
    private val onPopupMenuClicked: (View, WineyFeed) -> Unit,
    private val toFeedDetail: (WineyFeed) -> Unit
) : PagingDataAdapter<WineyFeed, WineyFeedAdapter.WineyFeedViewHolder>(diffUtil) {

    inner class WineyFeedViewHolder(
        private val binding: ItemWineyfeedPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: WineyFeed?) {
            binding.apply {
                this.data = data
                if (data == null) return

                ivWineyfeedLike.setOnSingleClickListener {
                    onlikeButtonClicked(data)
                }

                btnWineyfeedMore.setOnSingleClickListener { view ->
                    onPopupMenuClicked(view, data)
                }

                lWineyfeedPost.setOnSingleClickListener {
                    toFeedDetail(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WineyFeedViewHolder {
        val binding =
            ItemWineyfeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WineyFeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WineyFeedViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun updateItem(feedId: Int, isLiked: Boolean, likes: Int): WineyFeed? {
        snapshot().items.forEachIndexed { index, wineyFeed ->
            if (wineyFeed.feedId == feedId) {
                wineyFeed.isLiked = isLiked
                wineyFeed.likes = likes.toLong()
                notifyItemChanged(index)
                return wineyFeed
            }
        }
        return null
    }

    fun updateSelectedItem(detailFeed: DetailFeed): WineyFeed? {
        snapshot().items.forEachIndexed { index, wineyFeed ->
            if (wineyFeed.feedId == detailFeed.feedId) {
                wineyFeed.isLiked = detailFeed.isLiked
                wineyFeed.likes = detailFeed.likes
                wineyFeed.comments = detailFeed.comments
                notifyItemChanged(index)
                return wineyFeed
            }
        }
        return null
    }

    fun deleteItem(feedId: Int): MutableList<WineyFeed> {
        val currentList = snapshot().items
        val newList = currentList.toMutableList()
        val feed = currentList.find { it.feedId == feedId }
        newList.remove(feed)
        return newList
    }

    companion object {
        private val diffUtil = ItemDiffCallback<WineyFeed>(
            onItemsTheSame = { old, new -> old.feedId == new.feedId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
