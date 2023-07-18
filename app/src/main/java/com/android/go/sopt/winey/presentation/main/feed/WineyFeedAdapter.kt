package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ItemWineyfeedPostBinding
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.util.view.ItemDiffCallback
import com.android.go.sopt.winey.util.view.setOnSingleClickListener

class WineyFeedAdapter(
    private val likeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit
) :
    ListAdapter<WineyFeed, WineyFeedAdapter.WineyFeedViewHolder>(diffUtil) {

    class WineyFeedViewHolder(
        private val binding: ItemWineyfeedPostBinding,
        private val onLikeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: WineyFeed) {
            val isLiked = data.isLiked
            binding.ivWineyfeedLike.setImageResource(
                if (isLiked) R.drawable.ic_wineyfeed_liked else R.drawable.ic_wineyfeed_disliked
            )
            binding.data = data
            binding.ivWineyfeedLike.apply{
                setOnSingleClickListener {
                    val feedId = data.feedId
                    onLikeButtonClick(feedId,!isLiked)
                }
                setImageResource(
                    if (isLiked) R.drawable.ic_wineyfeed_liked else R.drawable.ic_wineyfeed_disliked
                )
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WineyFeedViewHolder {
        val binding =
            ItemWineyfeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WineyFeedViewHolder(binding, likeButtonClick)
    }

    override fun onBindViewHolder(holder: WineyFeedViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {
        private val diffUtil = ItemDiffCallback<WineyFeed>(
            onItemsTheSame = { old, new -> old.feedId == new.feedId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}