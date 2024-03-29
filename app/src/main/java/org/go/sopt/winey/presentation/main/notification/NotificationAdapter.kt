package org.go.sopt.winey.presentation.main.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.go.sopt.winey.databinding.ItemNotificationPostBinding
import org.go.sopt.winey.domain.entity.Notification
import org.go.sopt.winey.presentation.model.NotificationType
import org.go.sopt.winey.util.view.ItemDiffCallback

class NotificationAdapter(
    private val navigateFeedDetail: (feedId: Int?) -> Unit,
    private val navigateMyPage: () -> Unit,
    private val navigateGoalPath: () -> Unit
) : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(DiffUtil) {

    fun setData(dataList: List<Notification>?) {
        val filteredDataList = filterData(dataList)
        submitList(filteredDataList)
    }

    private fun filterData(dataList: List<Notification>?): List<Notification>? {
        return dataList?.filterNot { data ->
            data.notiReceiver.length <= data.notiMessage.length &&
                data.notiReceiver == data.notiMessage.substring(0, data.notiReceiver.length)
        }
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationPostBinding,
        private val navigateFeedDetail: (feedId: Int?) -> Unit,
        private val navigateMyPage: () -> Unit,
        private val navigateGoalPath: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: Notification?) {
            binding.apply {
                this.data = data
                if (data == null) {
                    return
                }

                binding.root.setOnClickListener {
                    when (data.notiType) {
                        NotificationType.LIKE_NOTIFICATION.key,
                        NotificationType.COMMENT_NOTIFICATION.key -> navigateFeedDetail(data.linkId)
                        NotificationType.HOW_TO_LEVEL_UP.key -> navigateGoalPath.invoke()
                        else -> navigateMyPage.invoke()
                    }
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding, navigateFeedDetail, navigateMyPage, navigateGoalPath)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {
        private val DiffUtil = ItemDiffCallback<Notification>(
            onItemsTheSame = { old, new -> old.notiId == new.notiId },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
