package com.android.go.sopt.winey.data.model.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody

@Serializable
data class RequestPostWineyFeedDto(
    @SerialName("feedImage")
    val feedImage: MultipartBody.Part,
    @SerialName("feedTitle")
    val feedTitle: String,
    @SerialName("feedMoney")
    val feedMoney: Long
)
