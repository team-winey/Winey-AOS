package com.android.go.sopt.winey.presentation.main.mypage.myfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemMyfeedPostBinding
import com.android.go.sopt.winey.databinding.ItemWineyfeedPostBinding
import com.android.go.sopt.winey.domain.model.WineyFeedModel
import com.android.go.sopt.winey.util.view.ItemDiffCallback

class MyFeedAdapter :
    ListAdapter<WineyFeedModel, MyFeedAdapter.MyFeedViewHolder>(diffUtil) {

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
        return MyFeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyFeedViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {
        private val diffUtil = ItemDiffCallback<WineyFeedModel>(
            onItemsTheSame = { old, new -> old.feedId == new.feedId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}