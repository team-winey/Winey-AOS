package com.android.go.sopt.winey.data.repository

import com.android.go.sopt.winey.data.source.RecommendDataSource
import com.android.go.sopt.winey.domain.entity.Recommend
import com.android.go.sopt.winey.domain.repository.RecommendRepository
import javax.inject.Inject

class RecommendRepositoryImpl @Inject constructor(
    private val recommendDataSource: RecommendDataSource
) : RecommendRepository {
    override suspend fun getRecommendList(page: Int): Result<List<Recommend>?> =
        runCatching {
            recommendDataSource.getRecommendList(page).data?.convertToRecommend()
        }
}
