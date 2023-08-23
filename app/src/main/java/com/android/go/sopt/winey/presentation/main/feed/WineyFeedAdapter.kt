package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ItemWineyfeedPostBinding
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.util.view.ItemDiffCallback
import com.android.go.sopt.winey.util.view.setOnSingleClickListener
import kotlin.properties.Delegates

class WineyFeedAdapter(
    private val onlikeButtonClicked: (WineyFeed: WineyFeed) -> Unit,
    private val onPopupMenuClicked: (View, WineyFeed: WineyFeed) -> Unit,
    private val toFeedDetail: (WineyFeed: WineyFeed) -> Unit
) : PagingDataAdapter<WineyFeed, WineyFeedAdapter.WineyFeedViewHolder>(diffUtil) {

    class WineyFeedViewHolder(
        private val binding: ItemWineyfeedPostBinding,
        private val onlikeButtonClicked: (WineyFeed: WineyFeed) -> Unit,
        private val onPopupMenuClicked: (View, WineyFeed: WineyFeed) -> Unit,
        private val toFeedDetail: (WineyFeed: WineyFeed) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: WineyFeed?) {
            binding.apply {
                this.data = data
                if (data == null) {
                    return
                }
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
        return WineyFeedViewHolder(binding, onlikeButtonClicked, onPopupMenuClicked, toFeedDetail)
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
