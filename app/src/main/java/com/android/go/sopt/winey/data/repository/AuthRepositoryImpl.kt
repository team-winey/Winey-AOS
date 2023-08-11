package com.android.go.sopt.winey.data.repository

import WineyFeedPagingSource
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.android.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.android.go.sopt.winey.data.service.AuthService
import com.android.go.sopt.winey.data.source.AuthDataSource
import com.android.go.sopt.winey.domain.entity.Goal
import com.android.go.sopt.winey.domain.entity.Like
import com.android.go.sopt.winey.domain.entity.Recommend
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val authService: AuthService
) : AuthRepository {
    override suspend fun getUser(): Result<User?> =
        runCatching {
            authDataSource.getUser().data?.toUser()
        }

    override suspend fun getWineyFeedList(): Flow<PagingData<WineyFeed>> =
        Pager(PagingConfig(FEED_PAGE_SIZE, prefetchDistance = LOAD_DISTANCE)) {
            WineyFeedPagingSource(authService)
        }.flow

    override suspend fun getMyFeedList(page: Int): Result<List<WineyFeed>> =
        runCatching {
            val response = authDataSource.getMyFeedList(page)
            response.toWineyFeed()
        }

    override suspend fun postWineyFeed(
        file: MultipartBody.Part?,
        requestMap: HashMap<String, RequestBody>
    ): Result<ResponsePostWineyFeedDto?> =
        runCatching {
            authDataSource.postWineyFeedList(file, requestMap).data
        }

    override suspend fun deleteFeed(feedId: Int): Result<Unit> =
        runCatching {
            authDataSource.deleteFeed(feedId)
        }

    override suspend fun postFeedLike(
        feedId: Int,
        requestPostLikeDto: RequestPostLikeDto
    ): Result<Like> =
        runCatching {
            authDataSource.postFeedLike(feedId, requestPostLikeDto).toLike()
        }

    override suspend fun postCreateGoal(requestCreateGoalDto: RequestCreateGoalDto): Result<Goal?> =
        runCatching {
            authDataSource.postCreateGoal(requestCreateGoalDto).data?.toGoal()
        }

    override suspend fun getRecommendList(page: Int): Result<List<Recommend>?> =
        runCatching {
            authDataSource.getRecommendList(page).data?.convertToRecommend()
        }

    override suspend fun postLogin(
        socialAccessToken: String,
        requestLoginDto: RequestLoginDto
    ): Result<ResponseLoginDto?> =
        runCatching {
            authDataSource.postLogin(socialAccessToken, requestLoginDto).data
        }

    override suspend fun postReIssueToken(refreshToken: String): Result<ResponseReIssueTokenDto?> =
        runCatching {
            authDataSource.postReIssueToken(refreshToken).data
        }

    companion object {
        const val FEED_PAGE_SIZE = 20
        const val LOAD_DISTANCE = 2
    }
}
