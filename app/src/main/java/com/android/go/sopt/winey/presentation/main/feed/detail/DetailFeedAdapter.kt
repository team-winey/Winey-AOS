package com.android.go.sopt.winey.presentation.main.feed.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemDetailFeedBinding
import com.android.go.sopt.winey.domain.entity.DetailFeed
import com.android.go.sopt.winey.util.view.setOnSingleClickListener

class DetailFeedAdapter(
    private val detailFeed: DetailFeed,
    private val postLike: (feedId: Int, isLiked: Boolean) -> Unit
) :
    RecyclerView.Adapter<DetailFeedAdapter.DetailFeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailFeedViewHolder {
        val binding =
            ItemDetailFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailFeedViewHolder(binding, postLike)
    }

    override fun getItemCount(): Int = FEED_ITEM_COUNT

    override fun onBindViewHolder(holder: DetailFeedViewHolder, position: Int) {
        holder.bind(detailFeed)
    }

    fun updateCommentNumber(comments: Long) {
        detailFeed.comments = comments
        notifyItemChanged(0)
    }

    class DetailFeedViewHolder(
        private val binding: ItemDetailFeedBinding,
        private val postLike: (feedId: Int, isLiked: Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DetailFeed?) {
            binding.apply {
                this.data = data
                if (data == null) {
                    return
                }
                ivDetailLike.setOnSingleClickListener {
                    postLike(data.feedId, !data.isLiked)
                }
            }
        }
    }

    companion object {
        private const val FEED_ITEM_COUNT = 1
    }
}
