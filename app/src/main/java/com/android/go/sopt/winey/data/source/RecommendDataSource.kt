package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.model.remote.response.ResponseGetRecommendListDto
import com.android.go.sopt.winey.data.model.remote.response.base.BaseResponse
import com.android.go.sopt.winey.data.service.RecommendService
import javax.inject.Inject

class RecommendDataSource @Inject constructor(
    private val recommendService: RecommendService
) {
    suspend fun getRecommendList(page: Int): BaseResponse<ResponseGetRecommendListDto> =
        recommendService.getRecommendList(page)
}
