package com.android.go.sopt.winey.presentation.main.feed

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ItemWineyfeedPostBinding
import com.android.go.sopt.winey.databinding.ProgressBarLoadingBinding
import com.android.go.sopt.winey.util.view.setOnSingleClickListener

sealed class FeedMultiViewHolder<E : FeedMultiViewItem>(
    binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {

    data class LoadingViewHolder(
        private val binding: ProgressBarLoadingBinding
    ) : FeedMultiViewHolder<FeedMultiViewItem.Loading>(binding) {
        override fun bind(item: FeedMultiViewItem.Loading) {
        }
    }

    data class FeedViewHolder(
        private val binding: ItemWineyfeedPostBinding,
        private val onLikeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit,
        private val showPopupMenu: (View, FeedMultiViewItem.WineyFeed) -> Unit
    ) : FeedMultiViewHolder<FeedMultiViewItem.WineyFeed>(binding) {
        override fun bind(item: FeedMultiViewItem.WineyFeed) {
            binding.apply {
                this.data = item
                ivWineyfeedProfilephoto.setImageResource(setUserProfile(item.writerLevel))
                ivWineyfeedLike.setImageResource(
                    if (item.isLiked) R.drawable.ic_wineyfeed_liked else R.drawable.ic_wineyfeed_disliked
                )
                ivWineyfeedLike.setOnSingleClickListener {
                    onLikeButtonClick(item.feedId, !item.isLiked)
                }

                btnWineyfeedMore.setOnClickListener { view ->
                    showPopupMenu(view, item)
                }
                executePendingBindings()
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

    abstract fun bind(item: E)
}
