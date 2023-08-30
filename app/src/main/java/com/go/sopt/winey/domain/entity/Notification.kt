package com.go.sopt.winey.domain.entity

data class Notification(
    val notiId: Int,
    val notiReceiver: String,
    val notiMessage: String,
    val notiType: String,
    val isChecked: Boolean,
    val timeAgo: String,
    val linkId: Int?
)
