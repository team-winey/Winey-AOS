package org.go.sopt.winey.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.go.sopt.winey.domain.entity.UserV2
import org.go.sopt.winey.domain.repository.DataStoreRepository
import java.io.IOException
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {

    override suspend fun saveSocialToken(socialAccessToken: String, socialRefreshToken: String) {
        dataStore.edit {
            it[SOCIAL_ACCESS_TOKEN] = socialAccessToken
            it[SOCIAL_REFRESH_TOKEN] = socialRefreshToken
        }
    }

    override suspend fun getSocialAccessToken(): Flow<String?> {
        return getStringValue(SOCIAL_ACCESS_TOKEN)
    }

    override suspend fun saveAccessToken(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[ACCESS_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun saveDeviceToken(deviceToken: String) {
        dataStore.edit {
            it[DEVICE_TOKEN] = deviceToken
        }
    }

    override suspend fun saveUserId(userId: Int) {
        dataStore.edit {
            it[USER_ID] = userId
        }
    }

    override suspend fun getAccessToken(): Flow<String?> {
        return getStringValue(ACCESS_TOKEN)
    }

    override suspend fun getRefreshToken(): Flow<String?> {
        return getStringValue(REFRESH_TOKEN)
    }

    override suspend fun getDeviceToken(): Flow<String?> {
        return getStringValue(DEVICE_TOKEN)
    }

    override suspend fun getStringValue(key: Preferences.Key<String>): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map {
                it[key]
            }
    }

    override suspend fun getUserId(): Flow<Int?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map {
                it[USER_ID]
            }
    }

    override suspend fun saveUserInfo(userInfo: UserV2?) {
        dataStore.edit {
            val json = Gson().toJson(userInfo)
            it[USER_INFO] = json
        }
    }

    override suspend fun getUserInfo(): Flow<UserV2> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map {
                val json = it[USER_INFO]
                try {
                    Gson().fromJson(json, UserV2::class.java)
                } catch (e: Exception) {
                    UserV2(
                        nickname = "",
                        userLevel = "",
                        fcmIsAllowed = true,
                        accumulatedAmount = 0,
                        accumulatedCount = 0,
                        createdDday = 0,
                        amountSavedTwoWeeks = 0,
                        amountSpentTwoWeeks = 0,
                        remainingCount = 0,
                        remainingAmount = 0,
                        isLevelUpAmountConditionMet = false,
                        isLevelUpCountConditionMet = false
                    )
                }
            }
    }

    override suspend fun clearDataStore() {
        dataStore.edit { it.clear() }
    }

    companion object PreferencesKeys {
        private val SOCIAL_ACCESS_TOKEN: Preferences.Key<String> =
            stringPreferencesKey("social_access_token")
        private val SOCIAL_REFRESH_TOKEN: Preferences.Key<String> =
            stringPreferencesKey("social_refresh_token")
        private val ACCESS_TOKEN: Preferences.Key<String> = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN: Preferences.Key<String> = stringPreferencesKey("refresh_token")
        private val DEVICE_TOKEN: Preferences.Key<String> = stringPreferencesKey("device_token")
        private val USER_ID: Preferences.Key<Int> = intPreferencesKey("user_id")
        private val USER_INFO: Preferences.Key<String> = stringPreferencesKey("user_info")
    }
}
