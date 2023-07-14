package com.android.go.sopt.winey.data.model.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetUserDto(
    @SerialName("code")
    val code : Int,
    @SerialName("message")
    val message : String,
    @SerialName("data")
    val data : Data,
){
    @Serializable
    data class Data(
        @SerialName("user")
        val user : User,
        @SerialName("goal")
        val goal : Goal,
    ){
        @Serializable
        data class User(
            @SerialName("userId")
            val userId : Int,
            @SerialName("nickname")
            val nickname : String,
            @SerialName("userLevel")
            val userLevel : String,
        )
        @Serializable
        data class Goal(
            @SerialName("duringGoalAmount")
            val duringGoalAmount : Int,
            @SerialName("duringGoalCount")
            val duringGoalCount : Int,
            @SerialName("targetMoney")
            val targetMoney : Int,
            @SerialName("targetDay")
            val targetDay : Int,
            @SerialName("isOver")
            val isOver : Boolean,
            @SerialName("isAttained")
            val isAttained : Boolean
        )
    }
}
