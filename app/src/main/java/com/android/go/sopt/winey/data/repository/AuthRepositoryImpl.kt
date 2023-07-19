package com.android.go.sopt.winey.data.repository

import com.android.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.data.source.AuthDataSource
import com.android.go.sopt.winey.domain.entity.Goal
import com.android.go.sopt.winey.domain.entity.Like
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.entity.WineyFeed
import com.android.go.sopt.winey.domain.repository.AuthRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override suspend fun getUser(): Result<User> =
        runCatching {
            authDataSource.getUser().data!!.toUser()
        }

    override suspend fun getWineyFeedList(page: Int): Result<List<WineyFeed>> =
        runCatching {
            authDataSource.getWineyFeedList(page).toWineyFeed()
        }

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
        
    override suspend fun postFeedLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto): Result<Like> =
        runCatching {
            authDataSource.postFeedLike(feedId,requestPostLikeDto).toLike()
        }

    override suspend fun postCreateGoal(requestCreateGoalDto: RequestCreateGoalDto): Result<Goal> =
        runCatching {
            authDataSource.postCreateGoal(requestCreateGoalDto).data!!.toGoal()
        }
}