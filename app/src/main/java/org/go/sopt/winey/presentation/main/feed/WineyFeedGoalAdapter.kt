package org.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.databinding.ItemWineyfeedGoalBinding

class WineyFeedGoalAdapter : RecyclerView.Adapter<WineyFeedGoalAdapter.WineyFeedGoalViewHolder>() {
    class WineyFeedGoalViewHolder(
        binding: ItemWineyfeedGoalBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WineyFeedGoalViewHolder {
        return WineyFeedGoalViewHolder(
            binding = ItemWineyfeedGoalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WineyFeedGoalViewHolder, position: Int) {}
    override fun getItemCount(): Int = ITEM_COUNT

    companion object {
        private const val ITEM_COUNT = 1
    }
}
