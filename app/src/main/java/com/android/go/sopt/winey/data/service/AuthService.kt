package com.android.go.sopt.winey.data.service

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetWineyFeed
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AuthService {
    @GET("feed")
    suspend fun getWineyFeedList(
        @Query("page") page:Int
    ):ResponseGetWineyFeed
}