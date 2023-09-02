package com.go.sopt.winey.data.service

import com.go.sopt.winey.data.model.remote.request.RequestPostCommentDto
import com.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.go.sopt.winey.data.model.remote.response.ResponseDeleteCommentDto
import com.go.sopt.winey.data.model.remote.response.ResponseGetFeedDetailDto
import com.go.sopt.winey.data.model.remote.response.ResponseGetWineyFeedListDto
import com.go.sopt.winey.data.model.remote.response.ResponsePostCommentDto
import com.go.sopt.winey.data.model.remote.response.ResponsePostLikeDto
import com.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.go.sopt.winey.data.model.remote.response.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {
    @GET("feed")
    suspend fun getWineyFeedList(
        @Query("page") page: Int
    ): ResponseGetWineyFeedListDto

    @GET("feed/myFeed")
    suspend fun getMyFeedList(
        @Query("page") page: Int
    ): ResponseGetWineyFeedListDto

    @POST("feedLike/{feedId}")
    suspend fun postFeedLike(
        @Path("feedId") feedId: Int,
        @Body requestPostLikeDto: RequestPostLikeDto
    ): ResponsePostLikeDto

    @DELETE("feed/{feedId}")
    suspend fun deleteFeed(
        @Path("feedId") feedId: Int
    ): BaseResponse<Unit>

    @Multipart
    @POST("feed")
    suspend fun postWineyFeed(
        @Part file: MultipartBody.Part?,
        @PartMap requestMap: HashMap<String, RequestBody>
    ): BaseResponse<ResponsePostWineyFeedDto>

    @GET("feed/{feedId}")
    suspend fun getFeedDetail(
        @Path("feedId") feedId: Int
    ): BaseResponse<ResponseGetFeedDetailDto>

    @POST("comment/{feedId}")
    suspend fun postComment(
        @Path("feedId") feedId: Int,
        @Body requestPostCommentDto: RequestPostCommentDto
    ): BaseResponse<ResponsePostCommentDto>

    @DELETE("comment/{commentId}")
    suspend fun deleteComment(
        @Path("commentId") commentId: Long
    ): BaseResponse<ResponseDeleteCommentDto>
}