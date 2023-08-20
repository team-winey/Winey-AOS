package com.android.go.sopt.winey.presentation.main.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemNotificationPostBinding
import com.android.go.sopt.winey.domain.entity.Notification
import com.android.go.sopt.winey.util.view.ItemDiffCallback

class NotificationAdapter(
    private val navigateFeedDetail: (feedId: Int?) -> Unit,
    private val navigateMypage: () -> Unit,
    private val navigateLevelupHelp: () -> Unit
) :
    ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(DiffUtil) {

    class NotificationViewHolder(
        private val binding: ItemNotificationPostBinding,
        private val navigateFeedDetail: (feedId: Int?) -> Unit,
        private val navigateMypage: () -> Unit,
        private val navigateLevelupHelp: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: Notification?) {
            binding.apply {
                this.data = data
                if(data == null){
                    return
                }
                binding.root.setOnClickListener {
                    when(data?.notiType){
                        "HOWTOLEVELUP" -> navigateLevelupHelp.invoke()
                        "LIKENOTI", "COMMENTNOTI" -> navigateFeedDetail(data.linkId)
                        else -> navigateMypage.invoke()
                    }
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding, navigateFeedDetail, navigateMypage, navigateLevelupHelp)
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
