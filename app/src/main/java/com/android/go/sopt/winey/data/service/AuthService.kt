package com.android.go.sopt.winey.data.service

import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetWineyFeedDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostLikeDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthService {
    @GET("/user")
    suspend fun getUser(): ResponseGetUserDto

    @GET("feed")
    suspend fun getWineyFeedList(
        @Query("page") page:Int
    ): ResponseGetWineyFeedDto

    @POST("feedLike/{feedId}")
    suspend fun postFeedLike(
        @Path("feedId") feedId: Int,
        @Body requestPostLikeDto: RequestPostLikeDto
    ): ResponsePostLikeDto
}