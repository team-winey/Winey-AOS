package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.WineyFeedModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetWineyFeed(
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
            val feedMoney: Int,
            @SerialName("feedTitle")
            val feedTitle: String,
            @SerialName("isLiked")
            val isLiked: Boolean,
            @SerialName("likes")
            val likes: Int,
            @SerialName("nickName")
            val nickName: String,
            @SerialName("userId")
            val userId: Int,
            @SerialName("writerLevel")
            val writerLevel: Int
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

    fun convertToWineyFeedModel() = data.getFeedResponseDtoList.map { feed ->
        WineyFeedModel(
            feedTitle = feed.feedTitle,
            feedMoney = feed.feedMoney,
            feedImage = feed.feedImage,
            feedId = feed.feedId,
            isLiked = feed.isLiked,
            likes = feed.likes,
            nickName = feed.nickName,
            userId = feed.userId,
            writerLevel = feed.writerLevel
        )
    }
}