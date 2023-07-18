package com.android.go.sopt.winey.data.model.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPostLikeDto(
    @SerialName("feedLike")
    val feedLike : Boolean
)
