package com.android.go.sopt.winey.data.source

import com.android.go.sopt.winey.data.model.remote.request.RequestCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.request.RequestLoginDto
import com.android.go.sopt.winey.data.model.remote.request.RequestPatchNicknameDto
import com.android.go.sopt.winey.data.model.remote.request.RequestPostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseCreateGoalDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetNicknameDuplicateCheckDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetRecommendListDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetUserDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetWineyFeedListDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseLoginDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostLikeDto
import com.android.go.sopt.winey.data.model.remote.response.ResponsePostWineyFeedDto
import com.android.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.android.go.sopt.winey.data.model.remote.response.base.BaseResponse
import com.android.go.sopt.winey.data.service.AuthService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val authService: AuthService
) {
    suspend fun getUser(): BaseResponse<ResponseGetUserDto?> = authService.getUser()

    suspend fun getWineyFeedList(page: Int): ResponseGetWineyFeedListDto =
        authService.getWineyFeedList(page)

    suspend fun getMyFeedList(page: Int): ResponseGetWineyFeedListDto =
        authService.getMyFeedList(page)

    suspend fun postFeedLike(
        feedId: Int,
        requestPostLikeDto: RequestPostLikeDto
    ): ResponsePostLikeDto =
        authService.postFeedLike(feedId, requestPostLikeDto)

    suspend fun postWineyFeedList(
        file: MultipartBody.Part?,
        requestMap: HashMap<String, RequestBody>
    ): BaseResponse<ResponsePostWineyFeedDto> =
        authService.postWineyFeed(file, requestMap)

    suspend fun postCreateGoal(requestCreateGoalDto: RequestCreateGoalDto): BaseResponse<ResponseCreateGoalDto> =
        authService.postCreateGoal(requestCreateGoalDto)

    suspend fun getRecommendList(page: Int): BaseResponse<ResponseGetRecommendListDto> =
        authService.getRecommendList(page)

    suspend fun deleteFeed(feedId: Int): BaseResponse<Unit> =
        authService.deleteFeed(feedId)

    suspend fun postLogin(
        socialAccessToken: String,
        requestLoginDto: RequestLoginDto
    ): BaseResponse<ResponseLoginDto> =
        authService.postLogin(socialAccessToken, requestLoginDto)

    suspend fun postReIssueToken(
        refreshToken: String
    ): BaseResponse<ResponseReIssueTokenDto> =
        authService.postReIssueToken(refreshToken)

    suspend fun getNicknameDuplicateCheck(nickname: String): BaseResponse<ResponseGetNicknameDuplicateCheckDto> =
        authService.getNicknameDuplicateCheck(nickname)

    suspend fun patchNickname(requestPatchNicknameDto: RequestPatchNicknameDto): BaseResponse<Nothing> =
        authService.patchNickname(requestPatchNicknameDto)
}
