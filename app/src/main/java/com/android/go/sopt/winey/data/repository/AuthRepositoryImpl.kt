package com.android.go.sopt.winey.data.repository

import android.util.Log
import com.android.go.sopt.winey.data.model.remote.request.RequestGetWineyFeed
import com.android.go.sopt.winey.data.model.remote.response.ResponseGetWineyFeed
import com.android.go.sopt.winey.data.service.AuthService
import com.android.go.sopt.winey.data.source.AuthDataSource
import com.android.go.sopt.winey.domain.entity.WineyFeedModel
import com.android.go.sopt.winey.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthDataSource
): AuthRepository{
    override suspend fun getWineyFeed(userId:Int, page:Int): Result<List<WineyFeedModel>> =
        runCatching{
            val response = dataSource.getWineyFeed(userId, page)
            Log.e("response", response.toString())
            Log.e("converted",response.convertToModel().toString())
            response.convertToModel()
        }
}