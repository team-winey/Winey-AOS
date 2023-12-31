package org.go.sopt.winey.data.service

import org.go.sopt.winey.data.model.remote.response.ResponseGetRecommendListDto
import org.go.sopt.winey.data.model.remote.response.base.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecommendService {
    @GET("recommend")
    suspend fun getRecommendList(
        @Query("page") page: Int
    ): BaseResponse<ResponseGetRecommendListDto>
}
