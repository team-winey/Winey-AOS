package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.Comment
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
) {
    fun toComment() = Comment(
        commentId = this.commentId,
        author = this.author,
        content = this.content,
        authorLevel = this.authorLevel,
        authorId = this.authorId
    )
}
