package com.go.sopt.winey.domain.repository

import androidx.datastore.preferences.core.Preferences
import com.go.sopt.winey.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveSocialToken(socialAccessToken: String, socialRefreshToken: String)

    suspend fun getSocialAccessToken(): Flow<String?>

    suspend fun saveAccessToken(accessToken: String = "", refreshToken: String = "")

    suspend fun saveUserId(userId: Int = 0)

    suspend fun getAccessToken(): Flow<String?>

    suspend fun getRefreshToken(): Flow<String?>

    suspend fun getStringValue(key: Preferences.Key<String>): Flow<String?>

    suspend fun getUserId(): Flow<Int?>

    suspend fun saveUserInfo(userInfo: User?)

    suspend fun getUserInfo(): Flow<User?>

    suspend fun clearDataStore()
}
