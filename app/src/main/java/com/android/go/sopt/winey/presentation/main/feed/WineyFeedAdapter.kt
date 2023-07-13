package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemWineyfeedPostBinding
import com.android.go.sopt.winey.domain.model.WineyFeedModel
import com.android.go.sopt.winey.util.view.ItemDiffCallback

class WineyFeedAdapter :
    ListAdapter<WineyFeedModel, WineyFeedAdapter.WineyFeedViewHolder>(diffUtil) {

    class WineyFeedViewHolder(
        private val binding: ItemWineyfeedPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: WineyFeedModel) {
            binding.data = data
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WineyFeedViewHolder {
        val binding =
            ItemWineyfeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WineyFeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WineyFeedViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {
        private val diffUtil = ItemDiffCallback<WineyFeedModel>(
            onItemsTheSame = { old, new -> old.feedId == new.feedId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}