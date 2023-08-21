package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ItemWineyfeedPostBinding
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.util.view.ItemDiffCallback
import com.android.go.sopt.winey.util.view.setOnSingleClickListener

class WineyFeedAdapter(
    private val likeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit,
    private val showPopupMenu: (View, WineyFeed) -> Unit,
    private val toFeedDetail: (feedId: Int, writerLevel: Int) -> Unit
) : PagingDataAdapter<WineyFeed, WineyFeedAdapter.WineyFeedViewHolder>(diffUtil) {
    private val currentData: ItemSnapshotList<WineyFeed>
        get() = snapshot()

    class WineyFeedViewHolder(
        private val binding: ItemWineyfeedPostBinding,
        private val onLikeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit,
        private val showPopupMenu: (View, WineyFeed) -> Unit,
        private val toFeedDetail: (feedId: Int, writerId: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: WineyFeed?) {
            binding.apply {
                this.data = data
                if (data == null) {
                    return
                }
                ivWineyfeedProfilephoto.setImageResource(setUserProfile(data.writerLevel))
                ivWineyfeedLike.setOnSingleClickListener {
                    onLikeButtonClick(data.feedId, !data.isLiked)
                }

                btnWineyfeedMore.setOnSingleClickListener { view ->
                    showPopupMenu(view, data)
                }

                lWineyfeedPost.setOnSingleClickListener {
                    toFeedDetail(data.feedId, data.userId)
                }
            }
        }

        private fun setUserProfile(userLevel: Int): Int {
            return when (userLevel) {
                1 -> R.drawable.img_wineyfeed_profile_1
                2 -> R.drawable.img_wineyfeed_profile_2
                3 -> R.drawable.img_wineyfeed_profile_3
                4 -> R.drawable.img_wineyfeed_profile_4
                else -> R.drawable.img_wineyfeed_profile
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WineyFeedViewHolder {
        val binding =
            ItemWineyfeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WineyFeedViewHolder(binding, likeButtonClick, showPopupMenu, toFeedDetail)
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
