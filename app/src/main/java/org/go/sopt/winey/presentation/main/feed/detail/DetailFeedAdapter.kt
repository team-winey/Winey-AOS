package org.go.sopt.winey.presentation.main.feed.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.databinding.ItemDetailFeedBinding
import org.go.sopt.winey.domain.entity.DetailFeed
import org.go.sopt.winey.util.view.setOnSingleClickListener

class DetailFeedAdapter(
    private val detailFeed: DetailFeed,
    private val onLikeButtonClicked: (feedId: Int, isLiked: Boolean) -> Unit,
    private val onPopupMenuClicked: (View) -> Unit
) :
    RecyclerView.Adapter<DetailFeedAdapter.DetailFeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailFeedViewHolder {
        val binding =
            ItemDetailFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailFeedViewHolder(binding, onLikeButtonClicked, onPopupMenuClicked)
    }

    override fun getItemCount(): Int = FEED_ITEM_COUNT

    override fun onBindViewHolder(holder: DetailFeedViewHolder, position: Int) {
        holder.bind(detailFeed)
    }

    fun updateLikeNumber(isLiked: Boolean, likes: Long): DetailFeed {
        detailFeed.isLiked = isLiked // 이미지 변경
        detailFeed.likes = likes // 개수 변경
        notifyItemChanged(0)
        return detailFeed
    }

    fun updateCommentNumber(comments: Long) {
        detailFeed.comments = comments
        notifyItemChanged(0)
    }

    class DetailFeedViewHolder(
        private val binding: ItemDetailFeedBinding,
        private val onLikeButtonClicked: (feedId: Int, isLiked: Boolean) -> Unit,
        private val onPopupMenuClicked: (View) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DetailFeed?) {
            binding.apply {
                this.data = data
                if (data == null) {
                    return
                }
                ivDetailLike.setOnSingleClickListener {
                    onLikeButtonClicked(data.feedId, !data.isLiked)
                }
                btnDetailMore.setOnSingleClickListener { view ->
                    onPopupMenuClicked(view)
                }
            }
        }
    }

    companion object {
        private const val FEED_ITEM_COUNT = 1
    }
}
