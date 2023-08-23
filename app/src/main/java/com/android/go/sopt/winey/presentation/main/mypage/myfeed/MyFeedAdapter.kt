package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ItemMyfeedPostBinding
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.util.view.ItemDiffCallback
import com.android.go.sopt.winey.util.view.setOnSingleClickListener

class MyFeedAdapter(
    private val onlikeButtonClicked: (WineyFeed: WineyFeed) -> Unit,
    private val onPopupMenuClicked: (View, WineyFeed: WineyFeed) -> Unit,
    private val toFeedDetail: (WineyFeed: WineyFeed) -> Unit
) : PagingDataAdapter<WineyFeed, MyFeedAdapter.MyFeedViewHolder>(diffUtil) {
    private val currentData: ItemSnapshotList<WineyFeed>
        get() = snapshot()

    class MyFeedViewHolder(
        private val binding: ItemMyfeedPostBinding,
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
        return MyFeedViewHolder(binding, onlikeButtonClicked, onPopupMenuClicked, toFeedDetail)
    }

    override fun onBindViewHolder(holder: MyFeedViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {
        private val diffUtil = ItemDiffCallback<WineyFeed>(
            onItemsTheSame = { old, new -> old.feedId == new.feedId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
