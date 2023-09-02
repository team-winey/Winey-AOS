package org.go.sopt.winey.data.source

import org.go.sopt.winey.data.model.remote.response.ResponseGetRecommendListDto
import org.go.sopt.winey.data.model.remote.response.base.BaseResponse
import org.go.sopt.winey.data.service.RecommendService
import javax.inject.Inject

class RecommendDataSource @Inject constructor(
    private val recommendService: RecommendService
) {
    suspend fun getRecommendList(page: Int): BaseResponse<ResponseGetRecommendListDto> =
        recommendService.getRecommendList(page)
}
