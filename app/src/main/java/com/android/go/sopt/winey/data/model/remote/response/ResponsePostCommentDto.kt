package com.android.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostCommentDto(
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("commentCounter")
    val commentCounter: Long,
    @SerialName("author")
    val author: String,
    @SerialName("content")
    val content: String,
    @SerialName("authorId")
    val authorId: Int,
    @SerialName("authorLevel")
    val authorLevel: Int
)
