package org.go.sopt.winey.presentation.main.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.databinding.ItemRecommendPostBinding
import org.go.sopt.winey.domain.entity.Recommend
import org.go.sopt.winey.util.view.ItemDiffCallback
import org.go.sopt.winey.util.view.setOnSingleClickListener

class RecommendAdapter(
    private val onItemLinkClicked: (Int, String) -> Unit
) : ListAdapter<Recommend, RecommendAdapter.RecommendViewHolder>(diffUtil) {

    inner class RecommendViewHolder(
        private val binding: ItemRecommendPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: Recommend) {
            binding.apply {
                this.data = data
                btnRecommendLink.isEnabled = data.link != "null"

                btnRecommendLink.setOnSingleClickListener {
                    onItemLinkClicked(data.id, data.link)
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendViewHolder {
        val binding =
            ItemRecommendPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendViewHolder(binding)
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
