package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.Notification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetNotificationDto(
    @SerialName("getNotiResponseDtoList")
    val getNotiResponseDtoList: List<GetNotiResponseDto>
){
    @Serializable
    data class GetNotiResponseDto(
        @SerialName("notiId")
        val notiId: Int,
        @SerialName("notiReceiver")
        val notiReceiver: String,
        @SerialName("notiMessage")
        val notiMessage: String,
        @SerialName("notiType")
        val notiType: String,
        @SerialName("isChecked")
        val isChecked: Boolean,
        @SerialName("timeAgo")
        val timeAgo: String,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("linkId")
        val linkId: Int
    )

    fun toNotification(): List<Notification> = getNotiResponseDtoList.map { data ->
        Notification(
            notiId = data.notiId,
            notiReceiver = data.notiReceiver,
            notiMessage = data.notiMessage,
            notiType = data.notiType,
            isChecked = data.isChecked,
            timeAgo = data.timeAgo,
            linkId = data.linkId
        )
    }
}
