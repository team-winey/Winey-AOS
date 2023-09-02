package org.go.sopt.winey.data.model.remote.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.go.sopt.winey.domain.entity.Comment

@Serializable
data class ResponsePostCommentDto(
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("authorId")
    val authorId: Int,
    @SerialName("author")
    val author: String,
    @SerialName("content")
    val content: String,
    @SerialName("authorLevel")
    val authorLevel: Int,
    @SerialName("createdAt")
    val createdAt: LocalDateTime
) {
    fun toComment() = Comment(
        commentId = this.commentId,
        author = this.author,
        content = this.content,
        authorLevel = this.authorLevel,
        authorId = this.authorId
    )
}
