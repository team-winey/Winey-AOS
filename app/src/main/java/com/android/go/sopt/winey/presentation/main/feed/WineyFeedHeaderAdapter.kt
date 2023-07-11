package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemWineyfeedHeaderBinding

class WineyFeedHeaderAdapter : RecyclerView.Adapter<WineyFeedHeaderAdapter.HeaderViewHolder>() {

    class HeaderViewHolder(
        private val binding: ItemWineyfeedHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            ItemWineyfeedHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        return
    }

    override fun getItemCount() = 1
}