package org.go.sopt.winey.domain.entity

data class DetailFeed(
    val feedId: Int,
    val feedImage: String,
    val feedMoney: Long,
    val feedTitle: String,
    var isLiked: Boolean,
    var likes: Long,
    val nickName: String,
    val userId: Int,
    val writerLevel: Int,
    var comments: Long,
    val timeAgo: String,
    val commentList: List<Comment>
)

data class Comment(
    val commentId: Long,
    val author: String,
    val content: String,
    val authorLevel: Int,
    val authorId: Int
)
