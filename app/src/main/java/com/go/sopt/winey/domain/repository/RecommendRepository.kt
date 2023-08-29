package com.go.sopt.winey.domain.repository

import com.go.sopt.winey.domain.entity.Recommend

interface RecommendRepository {
    suspend fun getRecommendList(page: Int): Result<List<Recommend>?>
}
