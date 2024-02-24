package org.go.sopt.winey.presentation.main.feed

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ItemWineyfeedGoalBinding
import org.go.sopt.winey.domain.entity.UserV2
import timber.log.Timber
import kotlin.math.roundToInt

class WineyFeedGoalAdapter(
    private val context: Context,
    private val initialUser: UserV2
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

    inner class WineyFeedGoalViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.user = initialUser
            updateGoalProgressRate(initialUser)
        }
    }

    private fun updateGoalProgressRate(user: UserV2) {
        val userLevels = context.resources.getStringArray(R.array.user_level)
        val targetMoneys = context.resources.getIntArray(R.array.target_money)

        userLevels.forEachIndexed { index, level ->
            if (level == user.userLevel) {
                val progressRate = ((user.accumulatedAmount / (targetMoneys[index] * 10000).toFloat()) * 100).roundToInt()
                binding.pbWineyfeedGoal.progress = progressRate
                return@forEachIndexed
            }
        }
    }

    fun updateUserInfo(updatedUser: UserV2) {
        if (::binding.isInitialized) {
            binding.user = updatedUser
            updateGoalProgressRate(updatedUser)
        }
    }

    companion object {
        private const val ITEM_COUNT = 1
    }
}
