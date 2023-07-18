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


class MyFeedAdapter(private val fragmentManager: FragmentManager) :
    ListAdapter<WineyFeed, MyFeedAdapter.MyFeedViewHolder>(diffUtil) {

    lateinit var myFeedDialogFragment: MyFeedDialogFragment

    class MyFeedViewHolder(
        private val binding: ItemMyfeedPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: WineyFeed) {
            binding.data = data
            binding.ivMyfeedLike.setImageResource(
                if (data.isLiked) R.drawable.ic_wineyfeed_liked else R.drawable.ic_wineyfeed_disliked
            )
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFeedViewHolder {
        val binding =
            ItemMyfeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvMyfeedDelete.setOnClickListener {
            showDeleteDialog()
        }
        return MyFeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyFeedViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    private fun showDeleteDialog() {
        myFeedDialogFragment = MyFeedDialogFragment()
        myFeedDialogFragment.show(fragmentManager, TAG_WINEYFEED_DIALOG)
    }

    companion object {
        private val diffUtil = ItemDiffCallback<WineyFeed>(
            onItemsTheSame = { old, new -> old.feedId == new.feedId },
            onContentsTheSame = { old, new -> old == new }
        )
        private const val TAG_WINEYFEED_DIALOG = "NO_GOAL_DIALOG"
    }

}