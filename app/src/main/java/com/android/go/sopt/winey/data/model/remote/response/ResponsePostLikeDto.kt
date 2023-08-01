package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.Like
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostLikeDto(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val data: Data,
    @SerialName("message")
    val message: String
) {
    @Serializable
    data class Data(
        @SerialName("feedId")
        val feedId: Int,
        @SerialName("isLiked")
        val isLiked: Boolean,
        @SerialName("likes")
        val likes: Int
    )

    fun toLike(): Like {
        val likeData = Like.Data(data.feedId, data.isLiked, data.likes)
        return Like(code, likeData, message)
    }
}
