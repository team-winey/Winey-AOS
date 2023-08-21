package com.android.go.sopt.winey.presentation.main.feed.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemDetailFeedBinding
import com.android.go.sopt.winey.domain.entity.DetailFeed

class DetailFeedAdapter(
    private val detailFeed: DetailFeed
) : RecyclerView.Adapter<DetailFeedAdapter.DetailFeedViewHolder>() {
    class DetailFeedViewHolder(
        private val binding: ItemDetailFeedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DetailFeed) {
            binding.data = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailFeedViewHolder {
        return DetailFeedViewHolder(
            ItemDetailFeedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = FEED_ITEM_COUNT

    override fun onBindViewHolder(holder: DetailFeedViewHolder, position: Int) {
        holder.bind(detailFeed)
    }

    fun updateCommentNumber(comments: Long) {
        detailFeed.comments = comments
        notifyItemChanged(0)
    }

    companion object {
        private const val FEED_ITEM_COUNT = 1
    }
}
