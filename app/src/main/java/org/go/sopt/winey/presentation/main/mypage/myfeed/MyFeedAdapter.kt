package org.go.sopt.winey.presentation.main.mypage.myfeed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.databinding.ItemMyfeedPostBinding
import org.go.sopt.winey.domain.entity.WineyFeed
import org.go.sopt.winey.util.view.ItemDiffCallback
import org.go.sopt.winey.util.view.setOnSingleClickListener

class MyFeedAdapter(
    private val onlikeButtonClicked: (WineyFeed) -> Unit,
    private val onPopupMenuClicked: (View, WineyFeed) -> Unit,
    private val toFeedDetail: (WineyFeed) -> Unit
) : PagingDataAdapter<WineyFeed, MyFeedAdapter.MyFeedViewHolder>(diffUtil) {

    inner class MyFeedViewHolder(
        private val binding: ItemMyfeedPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: WineyFeed?) {
            binding.apply {
                this.data = data
                if (data == null) {
                    return
                }
                ivMyfeedLike.setOnSingleClickListener {
                    onlikeButtonClicked(data)
                }
                btnWineyfeedMore.setOnClickListener { view ->
                    onPopupMenuClicked(view, data)
                }
                lMyfeedPost.setOnSingleClickListener {
                    toFeedDetail(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFeedViewHolder {
        val binding =
            ItemMyfeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyFeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyFeedViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun updateItem(feedId: Int, isLiked: Boolean, likes: Int) {
        snapshot().items.forEachIndexed { index, wineyFeed ->
            if (wineyFeed.feedId == feedId) {
                wineyFeed.isLiked = isLiked
                wineyFeed.likes = likes.toLong()
                notifyItemChanged(index)
            }
        }
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
