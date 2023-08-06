package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.ReIssueToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseReIssueToken(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String
){
    fun toReIssueToken(): ReIssueToken {
        val data = this

        return ReIssueToken(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken
        )
    }
}
