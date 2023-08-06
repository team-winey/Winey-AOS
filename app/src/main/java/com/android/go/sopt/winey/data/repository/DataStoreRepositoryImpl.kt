package com.android.go.sopt.winey.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.android.go.sopt.winey.data.repository.DataStoreRepositoryImpl.PreferencesKeys.ACCESS_TOKEN
import com.android.go.sopt.winey.data.repository.DataStoreRepositoryImpl.PreferencesKeys.REFRESH_TOKEN
import com.android.go.sopt.winey.data.repository.DataStoreRepositoryImpl.PreferencesKeys.SOCIAL_ACCESS_TOKEN
import com.android.go.sopt.winey.data.repository.DataStoreRepositoryImpl.PreferencesKeys.SOCIAL_REFRESH_TOKEN
import com.android.go.sopt.winey.domain.entity.User
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    val datastore: DataStore<Preferences>
) : DataStoreRepository {


    override suspend fun saveSocialToken(socialAccessToken: String, socialRefreshToken: String) {
        datastore.edit {
            it[SOCIAL_ACCESS_TOKEN] = socialAccessToken
            it[SOCIAL_REFRESH_TOKEN] = socialRefreshToken
        }
    }

    override suspend fun getSocialToken(): Flow<String?> {
        return datastore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map {
                it[SOCIAL_ACCESS_TOKEN]
            }
    }

    override suspend fun saveAccessToken(accessToken: String, refreshToken: String) {
        datastore.edit {
            it[ACCESS_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun getAccessToken(): Flow<String?> {
        return datastore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map {
                it[ACCESS_TOKEN]
            }
    }

    override suspend fun getRefreshToken(): Flow<String?> {
        return datastore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map {
                it[REFRESH_TOKEN]
            }
    }

    override suspend fun saveUserInfo(userInfo: User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(): Flow<User> {
        TODO("Not yet implemented")
    }

    private object PreferencesKeys {
        val SOCIAL_ACCESS_TOKEN: Preferences.Key<String> =
            stringPreferencesKey("social_access_token")
        val SOCIAL_REFRESH_TOKEN: Preferences.Key<String> =
            stringPreferencesKey("social_refresh_token")
        val ACCESS_TOKEN: Preferences.Key<String> = stringPreferencesKey("access_token")
        val REFRESH_TOKEN: Preferences.Key<String> = stringPreferencesKey("refresh_token")
    }
}
