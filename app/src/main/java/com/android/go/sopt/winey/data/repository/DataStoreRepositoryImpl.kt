package com.android.go.sopt.winey.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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

    override suspend fun saveUserId(userId: Int) {
        datastore.edit {
            it[USER_ID] = userId
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

    override suspend fun getUserId(): Flow<Int?> {
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
                it[USER_ID]
            }
    }

    override suspend fun saveUserInfo(userInfo: User) {
        datastore.edit {
            it[NICK_NAME] = userInfo.nickname
            it[USER_LEVEL] = userInfo.userLevel
            it[DURING_GOAL_AMOUNT] = userInfo.duringGoalAmount
            it[DURING_GOAL_COUNT] = userInfo.duringGoalCount
            it[TARGET_MONEY] = userInfo.targetMoney
            it[TARGET_DAY] = userInfo.targetDay
            it[D_DAY] = userInfo.dday
            it[IS_OVER] = userInfo.isOver
            it[IS_ATTAINED] = userInfo.isAttained
        }
    }

    override suspend fun getUserInfo(): Flow<User> {
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
                User(
                    nickname = it[NICK_NAME] ?: "",
                    userLevel = it[USER_LEVEL] ?: "",
                    duringGoalAmount = it[DURING_GOAL_AMOUNT] ?: 0,
                    duringGoalCount = it[DURING_GOAL_COUNT] ?: 0,
                    targetMoney = it[TARGET_MONEY] ?: 0,
                    targetDay = it[TARGET_DAY] ?: 0,
                    dday = it[D_DAY] ?: 0,
                    isOver = it[IS_OVER] ?: true,
                    isAttained = it[IS_ATTAINED] ?: false
                )
            }
    }

    companion object PreferencesKeys {
        private val SOCIAL_ACCESS_TOKEN: Preferences.Key<String> =
            stringPreferencesKey("social_access_token")
        private val SOCIAL_REFRESH_TOKEN: Preferences.Key<String> =
            stringPreferencesKey("social_refresh_token")
        private val ACCESS_TOKEN: Preferences.Key<String> = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN: Preferences.Key<String> = stringPreferencesKey("refresh_token")
        private val USER_ID: Preferences.Key<Int> = intPreferencesKey("user_id")
        private val NICK_NAME: Preferences.Key<String> = stringPreferencesKey("nick_name")
        private val USER_LEVEL: Preferences.Key<String> = stringPreferencesKey("user_level")
        private val DURING_GOAL_AMOUNT: Preferences.Key<Long> = longPreferencesKey("during_goal_amount")
        private val DURING_GOAL_COUNT: Preferences.Key<Long> = longPreferencesKey("during_goal_count")
        private val TARGET_MONEY: Preferences.Key<Int> = intPreferencesKey("target_money")
        private val TARGET_DAY: Preferences.Key<Int> = intPreferencesKey("target_day")
        private val D_DAY: Preferences.Key<Int> = intPreferencesKey("d_day")
        private val IS_OVER: Preferences.Key<Boolean> = booleanPreferencesKey("is_over")
        private val IS_ATTAINED: Preferences.Key<Boolean> = booleanPreferencesKey("is_attained")
    }
}
