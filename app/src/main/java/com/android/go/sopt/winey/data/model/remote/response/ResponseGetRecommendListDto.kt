package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.Recommend
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetRecommendListDto(
    @SerialName("pageResponseDto")
    val pageResponseDto: PageResponseDto,
    @SerialName("recommendsResponseDto")
    val recommendsResponseDto: List<RecommendsResponseDto>
) {
    @Serializable
    data class PageResponseDto(
        @SerialName("totalPageSize")
        val totalPageSize: Int,
        @SerialName("currentPageIndex")
        val currentPageIndex: Int,
        @SerialName("isEnd")
        val isEnd: Boolean
    )

    @Serializable
    data class RecommendsResponseDto(
        @SerialName("recommendId")
        val recommendId: Int?,
        @SerialName("recommendLink")
        val recommendLink: String?,
        @SerialName("recommendTitle")
        val recommendTitle: String?,
        @SerialName("recommendSubTitle")
        val recommendSubTitle: String?,
        @SerialName("recommendDiscount")
        val recommendDiscount: String?,
        @SerialName("recommendImage")
        val recommendImage: String?,
        @SerialName("createdAt")
        val createdAt: String?
    )

    fun convertToRecommend() = this.recommendsResponseDto.map {
        Recommend(
            id = it.recommendId ?: 0,
            link = it.recommendLink ?: "null",
            title = it.recommendTitle ?: "",
            subtitle = it.recommendSubTitle ?: "",
            discount = it.recommendDiscount ?: "",
            image = it.recommendImage ?: ""
        )
    }
}
