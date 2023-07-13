package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemMyfeedPostBinding
import com.android.go.sopt.winey.domain.entity.WineyFeedModel
import com.android.go.sopt.winey.util.view.ItemDiffCallback


class MyFeedAdapter(private val fragmentManager: FragmentManager) :
    ListAdapter<WineyFeedModel, MyFeedAdapter.MyFeedViewHolder>(diffUtil) {

    lateinit var myFeedDialogFragment: MyFeedDialogFragment

    class MyFeedViewHolder(
        private val binding: ItemMyfeedPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: WineyFeedModel) {
            binding.data = data
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
        private val diffUtil = ItemDiffCallback<WineyFeedModel>(
            onItemsTheSame = { old, new -> old.feedId == new.feedId },
            onContentsTheSame = { old, new -> old == new }
        )
        private const val TAG_WINEYFEED_DIALOG = "NO_GOAL_DIALOG"
    }

}