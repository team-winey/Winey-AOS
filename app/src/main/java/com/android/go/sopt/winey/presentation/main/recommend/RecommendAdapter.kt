package com.android.go.sopt.winey.presentation.main.recommend

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemRecommendPostBinding
import com.android.go.sopt.winey.domain.entity.Recommend
import com.android.go.sopt.winey.util.view.ItemDiffCallback
import com.android.go.sopt.winey.util.view.setOnSingleClickListener

class RecommendAdapter(
) : ListAdapter<Recommend, RecommendAdapter.RecommendViewHolder>(diffUtil) {

    class RecommendViewHolder(
        private val binding: ItemRecommendPostBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: Recommend) {
            binding.apply {
                this.data = data
                when (data.link == "null") {
                    true -> {
                        btnRecommendLink.isEnabled = false
                    }

                    false -> {
                        btnRecommendLink.isEnabled = true
                    }
                }
                btnRecommendLink.setOnSingleClickListener {
                    val url = data.link
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    it.context.startActivity(intent)
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendAdapter.RecommendViewHolder {
        val binding =
            ItemRecommendPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendAdapter.RecommendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {
        private val diffUtil = ItemDiffCallback<Recommend>(
            onItemsTheSame = { old, new -> old.id == new.id },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}