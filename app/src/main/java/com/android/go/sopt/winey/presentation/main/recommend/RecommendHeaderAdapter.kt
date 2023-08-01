package com.android.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemRecommendHeaderBinding

class RecommendHeaderAdapter : RecyclerView.Adapter<RecommendHeaderAdapter.HeaderViewHolder>() {

    class HeaderViewHolder(
        private val binding: ItemRecommendHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            ItemRecommendHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        return
    }

    override fun getItemCount(): Int = HEADER_COUNT

    companion object {
        private const val HEADER_COUNT = 1
    }
}
