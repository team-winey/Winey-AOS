package org.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.databinding.ItemWineyfeedGoalBinding
import org.go.sopt.winey.domain.entity.UserV2

class WineyFeedGoalAdapter(
    private val user: UserV2
) : RecyclerView.Adapter<WineyFeedGoalAdapter.WineyFeedGoalViewHolder>() {
    inner class WineyFeedGoalViewHolder(
        private val binding: ItemWineyfeedGoalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.user = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WineyFeedGoalViewHolder {
        return WineyFeedGoalViewHolder(
            binding = ItemWineyfeedGoalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WineyFeedGoalViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = ITEM_COUNT

    companion object {
        private const val ITEM_COUNT = 1
    }
}
