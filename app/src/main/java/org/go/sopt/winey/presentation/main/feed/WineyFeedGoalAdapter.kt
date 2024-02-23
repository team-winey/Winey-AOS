package org.go.sopt.winey.presentation.main.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.databinding.ItemWineyfeedGoalBinding
import org.go.sopt.winey.domain.entity.UserV2

class WineyFeedGoalAdapter(
    private val initialUserInfo: UserV2
) : RecyclerView.Adapter<WineyFeedGoalAdapter.WineyFeedGoalViewHolder>() {
    private lateinit var binding: ItemWineyfeedGoalBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WineyFeedGoalViewHolder {
        binding = ItemWineyfeedGoalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WineyFeedGoalViewHolder()
    }

    override fun onBindViewHolder(holder: WineyFeedGoalViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = ITEM_COUNT

    inner class WineyFeedGoalViewHolder: RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.user = initialUserInfo
        }
    }

    fun updateUserInfo(updatedUserInfo: UserV2) {
        if (::binding.isInitialized) {
            binding.user = updatedUserInfo
        }
    }

    companion object {
        private const val ITEM_COUNT = 1
    }
}
