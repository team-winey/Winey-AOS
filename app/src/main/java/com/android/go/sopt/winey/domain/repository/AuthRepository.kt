package com.android.go.sopt.winey.domain.repository

import androidx.paging.PagingData
import com.android.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.android.go.sopt.winey.domain.entity.Goal
import com.android.go.sopt.winey.domain.entity.Like
import com.android.go.sopt.winey.domain.entity.Recommend
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.entity.WineyFeed
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthRepository {
    suspend fun getUser(): Result<User?>

    suspend fun getWineyFeedList(): Flow<PagingData<WineyFeed>>

    suspend fun getMyFeedList(): Flow<PagingData<WineyFeed>>

    suspend fun postFeedLike(feedId: Int, requestPostLikeDto: RequestPostLikeDto): Result<Like>

    suspend fun postWineyFeed(
        file: MultipartBody.Part?,
        requestMap: HashMap<String, RequestBody>
    ): Result<ResponsePostWineyFeedDto?>

    suspend fun postCreateGoal(requestCreateGoalDto: RequestCreateGoalDto): Result<Goal?>

    suspend fun getRecommendList(page: Int): Result<List<Recommend>?>
    suspend fun deleteFeed(feedId: Int): Result<Unit>

    suspend fun postLogin(
        socialAccessToken: String,
        requestLoginDto: RequestLoginDto
    ): Result<ResponseLoginDto?>

    suspend fun postReIssueToken(refreshToken: String): Result<ResponseReIssueTokenDto?>

    suspend fun getNicknameDuplicateCheck(nickname: String): Result<ResponseGetNicknameDuplicateCheckDto?>
}
