package com.android.go.sopt.winey.domain.entity

data class Like(
    val code: Int,
    val data: Data,
    val message: String
) {
    data class Data(
        val feedId: Int,
        val isLiked: Boolean,
        val likes: Int
    )
}
