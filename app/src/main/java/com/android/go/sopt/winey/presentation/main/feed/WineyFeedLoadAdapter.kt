package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemFeedLoadingFooterBinding

class WineyFeedLoadAdapter() : LoadStateAdapter<WineyFeedLoadAdapter.LoadStateViewHolder>() {

    class LoadStateViewHolder(private val binding: ItemFeedLoadingFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(loadState: LoadState) {
            binding.apply {
                progressBarLoading.isVisible = loadState is LoadState.Loading
            }
        }
    }

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState
    ) {
        holder.onBind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadStateViewHolder {
        val binding =
            ItemFeedLoadingFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }

}
