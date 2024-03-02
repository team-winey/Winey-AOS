package org.go.sopt.winey.presentation.main.feed

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.R
import org.go.sopt.winey.databinding.ItemWineyfeedGoalBinding
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.presentation.model.UserLevel
import timber.log.Timber
import kotlin.math.roundToInt

class WineyFeedGoalAdapter(
    private val context: Context,
    private val initialUser: UserV2
) : RecyclerView.Adapter<WineyFeedGoalAdapter.WineyFeedGoalViewHolder>() {
    private lateinit var binding: ItemWineyfeedGoalBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WineyFeedGoalViewHolder {
        Timber.d("onCreateViewHolder")
        binding = ItemWineyfeedGoalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WineyFeedGoalViewHolder()
    }

    override fun onBindViewHolder(holder: WineyFeedGoalViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        holder.bind()
    }

    override fun getItemCount(): Int = ITEM_COUNT

    inner class WineyFeedGoalViewHolder : RecyclerView.ViewHolder(binding.root) {
        private var isInitialized = false

        fun bind() {
            if (!isInitialized) {
                isInitialized = true

                // 초기 유저 데이터로 최초 1회만 바인딩
                updateGoalProgressBar(initialUser)
            }
        }
    }

    fun updateGoalProgressBar(user: UserV2) {
        if (::binding.isInitialized) {
            // 피드 생성, 삭제에 따라 바뀌는 유저 데이터 반영
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
