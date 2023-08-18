package com.android.go.sopt.winey.domain.entity

data class WineyFeed(
    val feedId: Int,
    val feedImage: String,
    val feedMoney: Long,
    val feedTitle: String,
    var isLiked: Boolean,
    var likes: Long,
    val nickName: String,
    val userId: Int,
    val writerLevel: Int,
    val totalPageSize: Int,
    val isEnd: Boolean,
    val comments: Long,
    val timeAgo: String
)
