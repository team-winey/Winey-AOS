package com.android.go.sopt.winey.data.model.remote.response

import com.android.go.sopt.winey.domain.entity.Login
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLoginDto(
    @SerialName("userId")
    val userId: Int,
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("isRegistered")
    val isRegistered: Boolean
){
    fun toLogin(): Login{
        val data = this

        return Login(
            userId = this.userId,
            accessToken = this.accessToken,
            refreshToken = this.refreshToken,
            isRegistered = this.isRegistered
        )
    }
}
