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
    private val likeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit,
    private val showPopupMenu: (View, WineyFeed) -> Unit,
    private val toFeedDetail: (feedId: Int, writerId: Int) -> Unit
) : PagingDataAdapter<WineyFeed, MyFeedAdapter.MyFeedViewHolder>(diffUtil) {
    private val currentData: ItemSnapshotList<WineyFeed>
        get() = snapshot()

    class MyFeedViewHolder(
        private val binding: ItemMyfeedPostBinding,
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
                ivMyfeedProfilephoto.setImageResource(setUserProfile(data.writerLevel))
                ivMyfeedLike.setOnSingleClickListener {
                    onLikeButtonClick(data.feedId, !data.isLiked)
                }
                btnWineyfeedMore.setOnClickListener { view ->
                    showPopupMenu(view, data)
                }
                lMyfeedPost.setOnSingleClickListener {
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
                else -> {
                    R.drawable.img_wineyfeed_profile
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
        return MyFeedViewHolder(binding, likeButtonClick, showPopupMenu, toFeedDetail)
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
