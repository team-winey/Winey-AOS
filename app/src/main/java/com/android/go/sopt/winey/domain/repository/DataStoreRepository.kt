package com.android.go.sopt.winey.domain.repository

import com.android.go.sopt.winey.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveSocialToken(socialAccessToken: String, socialRefreshToken: String)

    suspend fun getSocialToken(): Flow<String?>

    suspend fun saveAccessToken(accessToken: String, refreshToken: String)

    suspend fun saveUserId(userId: Int)

    suspend fun getAccessToken(): Flow<String?>

    suspend fun getRefreshToken(): Flow<String?>

    suspend fun getUserId(): Flow<Int?>

    suspend fun saveUserInfo(userInfo: User)

    suspend fun getUserInfo(): Flow<User?>
}
