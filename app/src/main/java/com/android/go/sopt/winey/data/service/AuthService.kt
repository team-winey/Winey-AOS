package com.android.go.sopt.winey.data.service

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetWineyFeedListDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.data.model.remote.response.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface AuthService {
    @GET("user")
    suspend fun getUser(): ResponseGetUserDto

    @GET("feed")
    suspend fun getWineyFeedList(
        @Query("page") page: Int
    ): ResponseGetWineyFeedListDto

    @Multipart
    @POST("feed")
    suspend fun postWineyFeed(
        @Part file: MultipartBody.Part?,
        @PartMap requestMap: HashMap<String, RequestBody>
    ): BaseResponse<ResponsePostWineyFeedDto>
}