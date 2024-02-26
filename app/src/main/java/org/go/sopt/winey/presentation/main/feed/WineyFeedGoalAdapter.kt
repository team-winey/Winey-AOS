package org.go.sopt.winey.presentation.main.feed

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ItemWineyfeedGoalBinding
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.presentation.model.UserLevel
import kotlin.math.roundToInt

class WineyFeedGoalAdapter(
    private val context: Context,
    private val initialUser: UserV2
) : RecyclerView.Adapter<WineyFeedGoalAdapter.WineyFeedGoalViewHolder>() {
    private lateinit var binding: ItemWineyfeedGoalBinding
    private var isInitialized = false

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
            if (!isInitialized) {
                isInitialized = true

                // 최초 1회만 실행 (아이템 뷰의 재활용에 따라 예전 데이터가 다시 바인딩 되는 문제 해결)
                updateProgressBar(initialUser)
            }
        }
    }

    fun updateProgressBar(user: UserV2) {
        if (::binding.isInitialized) {
            binding.user = user
            updateProgressBarRate(user)
        }
    }

    private fun updateProgressBarRate(user: UserV2) {
        if (user.userLevel == UserLevel.FORTH.rankName) {
            binding.pbWineyfeedGoal.progress = 100
            return
        }

        val userLevels = context.resources.getStringArray(R.array.user_level)
        val targetMoneys = context.resources.getIntArray(R.array.target_money)
        userLevels.forEachIndexed { index, level ->
            if (level == user.userLevel) {
                val progressRate =
                    ((user.accumulatedAmount / (targetMoneys[index] * 10000).toFloat()) * 100).roundToInt()
                binding.pbWineyfeedGoal.progress = progressRate
                return@forEachIndexed
            }
        }
    }

    companion object {
        private const val ITEM_COUNT = 1
    }
}
