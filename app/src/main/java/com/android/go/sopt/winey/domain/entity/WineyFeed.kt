package com.android.go.sopt.winey.domain.entity


data class WineyFeed (
    val feedId: Int,
    val feedImage: String,
    val feedMoney: Long,
    val feedTitle: String,
    val isLiked: Boolean,
    val likes: Int,
    val nickName: String,
    val userId: Int,
    val writerLevel: Int,
    val totalPageSize: Int
)