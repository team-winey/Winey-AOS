package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.domain.entity.Recommend

interface RecommendRepository {
    suspend fun getRecommendList(page: Int): Result<List<Recommend>?>
}
