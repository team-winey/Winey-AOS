package org.go.sopt.winey.data.repository

import org.go.sopt.winey.data.source.RecommendDataSource
import org.go.sopt.winey.domain.entity.Recommend
import org.go.sopt.winey.domain.repository.RecommendRepository
import javax.inject.Inject

class RecommendRepositoryImpl @Inject constructor(
    private val recommendDataSource: RecommendDataSource
) : RecommendRepository {
    override suspend fun getRecommendList(page: Int): Result<List<Recommend>?> =
        runCatching {
            recommendDataSource.getRecommendList(page).data?.convertToRecommend()
        }
}
