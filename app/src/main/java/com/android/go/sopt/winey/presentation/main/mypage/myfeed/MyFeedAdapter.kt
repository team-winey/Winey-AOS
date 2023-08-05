package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.R
import com.android.go.sopt.winey.databinding.ItemMyfeedPostBinding
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.util.view.ItemDiffCallback
import com.android.go.sopt.winey.util.view.setOnSingleClickListener

class MyFeedAdapter(
    private val likeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit,
    private val fragmentManager: FragmentManager,
    private val deleteButtonClick: (feedId: Int, writerLevel: Int) -> Unit
) :
    ListAdapter<WineyFeed, MyFeedAdapter.MyFeedViewHolder>(diffUtil) {

    class MyFeedViewHolder(
        private val fragmentManager: FragmentManager,
        private val binding: ItemMyfeedPostBinding,
        private val onLikeButtonClick: (feedId: Int, isLiked: Boolean) -> Unit,
        private val deleteButtonClick: (feedId: Int, writerLevel: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: WineyFeed) {
            binding.apply {
                this.data = data
                ivMyfeedProfilephoto.setImageResource(setUserProfile(data.writerLevel))
                ivMyfeedLike.setImageResource(
                    if (data.isLiked) R.drawable.ic_wineyfeed_liked else R.drawable.ic_wineyfeed_disliked
                )
                ivMyfeedLike.setOnSingleClickListener {
                    onLikeButtonClick(data.feedId, !data.isLiked)
                }
                tvMyfeedDelete.setOnSingleClickListener {
                    showDeleteDialog(data.feedId, data.writerLevel)
                    deleteButtonClick(data.feedId, data.writerLevel)
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
                else -> {
                    R.drawable.img_wineyfeed_profile
                }
            }
        }

        private fun showDeleteDialog(feedId: Int, userLevel: Int) {
            val myFeedDeleteDialogFragment = MyFeedDeleteDialogFragment(feedId, userLevel)
            myFeedDeleteDialogFragment.show(fragmentManager, TAG_WINEYFEED_DIALOG)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFeedViewHolder {
        val binding =
            ItemMyfeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyFeedViewHolder(fragmentManager, binding, likeButtonClick, deleteButtonClick)
    }

    override fun onBindViewHolder(holder: MyFeedViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun updateLikeStatus(feedId: Int, isLiked: Boolean) {
        val index = currentList.indexOfFirst { it.feedId == feedId }
        if (index != -1) {
            currentList[index].isLiked = isLiked
            if (isLiked) {
                currentList[index].likes++
            } else {
                currentList[index].likes--
            }
            submitList(currentList)
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<WineyFeed>(
            onItemsTheSame = { old, new -> old.isLiked == new.isLiked },
            onContentsTheSame = { old, new -> old == new }
        )
        private const val TAG_WINEYFEED_DIALOG = "NO_GOAL_DIALOG"
    }
}
