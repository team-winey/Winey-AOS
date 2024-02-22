package org.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.go.sopt.winey.domain.entity.WineyFeed

@Serializable
data class ResponseGetWineyFeedListDto(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val data: Data,
    @SerialName("message")
    val message: String
) {
    @Serializable
    data class Data(
        @SerialName("getFeedResponseDtoList")
        val getFeedResponseDtoList: List<GetFeedResponseDto>,
        @SerialName("pageResponseDto")
        val pageResponseDto: PageResponseDto
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
            @SerialName("feedType")
            val feedType: String?,
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
        data class PageResponseDto(
            @SerialName("currentPageIndex")
            val currentPageIndex: Int,
            @SerialName("isEnd")
            val isEnd: Boolean,
            @SerialName("totalPageSize")
            val totalPageSize: Int
        )
    }

    fun toWineyFeed(): List<WineyFeed> = data.getFeedResponseDtoList.map { feed ->
        WineyFeed(
            feedId = feed.feedId,
            feedImage = feed.feedImage,
            feedMoney = feed.feedMoney,
            feedTitle = feed.feedTitle,
            feedType = feed.feedType,
            isLiked = feed.isLiked,
            likes = feed.likes,
            nickName = feed.nickName,
            userId = feed.userId,
            writerLevel = feed.writerLevel,
            comments = feed.comments,
            timeAgo = feed.timeAgo,
            totalPageSize = data.pageResponseDto.totalPageSize,
            isEnd = data.pageResponseDto.isEnd
        )
    }
}
