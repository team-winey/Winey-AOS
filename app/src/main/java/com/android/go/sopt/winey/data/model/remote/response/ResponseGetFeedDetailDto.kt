package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.CommentList
import com.android.go.sopt.winey.domain.entity.DetailFeed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetFeedDetailDto(
    @SerialName("getFeedResponseDto")
    val getFeedResponseDto: GetFeedResponseDto,
    @SerialName("getCommentResponseList")
    val getCommentResponseList: List<GetCommentResponseList>
) {
    @Serializable
    data class GetFeedResponseDto(
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("feedId")
        val feedId: Int,
        @SerialName("feedImage")
        val feedImage: String,
        @SerialName("feedMoney")
        val feedMoney: Long,
        @SerialName("feedTitle")
        val feedTitle: String,
        @SerialName("isLiked")
        val isLiked: Boolean,
        @SerialName("likes")
        val likes: Long,
        @SerialName("nickName")
        val nickName: String,
        @SerialName("userId")
        val userId: Int,
        @SerialName("writerLevel")
        val writerLevel: Int,
        @SerialName("comments")
        val comments: Long,
        @SerialName("timeAgo")
        val timeAgo: String
    )

    @Serializable
    data class GetCommentResponseList(
        @SerialName("commentId")
        val commentId: Long,
        @SerialName("author")
        val author: String,
        @SerialName("content")
        val content: String,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("authorLevel")
        val authorLevel: Int,
        @SerialName("authorId")
        val authorId: Int
    )

    fun toDetailFeed(): DetailFeed = DetailFeed(
        feedId = getFeedResponseDto.feedId,
        feedImage = getFeedResponseDto.feedImage,
        feedMoney = getFeedResponseDto.feedMoney,
        feedTitle = getFeedResponseDto.feedTitle,
        isLiked = getFeedResponseDto.isLiked,
        likes = getFeedResponseDto.likes,
        nickName = getFeedResponseDto.nickName,
        userId = getFeedResponseDto.userId,
        writerLevel = getFeedResponseDto.writerLevel,
        comments = getFeedResponseDto.comments,
        timeAgo = getFeedResponseDto.timeAgo,
        commentList = getCommentResponseList.map { list ->
            CommentList(
                commentId = list.commentId,
                author = list.author,
                content = list.content,
                authorId = list.authorId,
                authorLevel = list.authorLevel
            )
        }
    )
}