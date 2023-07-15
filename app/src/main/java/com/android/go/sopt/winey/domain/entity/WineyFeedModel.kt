package com.android.go.sopt.winey.domain.entity


data class WineyFeedModel (
    val feedId: Int,
    val feedImage: String,
    val feedMoney: Int,
    val feedTitle: String,
    val isLiked: Boolean,
    val likes: Int,
    val nickName: String,
    val userId: Int,
    val writerLevel: Int
)