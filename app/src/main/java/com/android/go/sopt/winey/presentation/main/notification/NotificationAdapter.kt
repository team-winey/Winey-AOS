package com.android.go.sopt.winey.presentation.main.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.go.sopt.winey.databinding.ItemNotificationPostBinding
import com.android.go.sopt.winey.domain.entity.Notification
import com.android.go.sopt.winey.util.view.ItemDiffCallback

class NotificationAdapter :
    ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(DiffUtil) {

    class NotificationViewHolder(private val binding: ItemNotificationPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: Notification?) {
            binding.apply {
                this.data = data

                binding.root.setOnClickListener {
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
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
