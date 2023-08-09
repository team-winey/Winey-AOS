package com.android.go.sopt.winey.data.interceptor

import android.content.Context
import com.android.go.sopt.winey.BuildConfig.AUTH_BASE_URL
import com.android.go.sopt.winey.data.model.remote.response.ResponseReIssueTokenDto
import com.android.go.sopt.winey.data.model.remote.response.base.BaseResponse
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    @ApplicationContext context: Context,
    private val json: Json,
    private val dataStoreRepository: DataStoreRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val headerRequest = originalRequest.newAuthBuilder()
            .build()

        val response = chain.proceed(headerRequest)

        when (response.code) {
            CODE_TOKEN_EXPIRED -> {
                try {
                    val refreshTokenRequest = originalRequest.newBuilder().post("".toRequestBody())
                        .url("$AUTH_BASE_URL/auth/token")
                        .addHeader(REFRESH_TOKEN, runBlocking(Dispatchers.IO) { getRefreshToken() })
                        .build()
                    val refreshTokenResponse = chain.proceed(refreshTokenRequest)
                    Timber.e("리프레시 토큰 : $refreshTokenResponse")

                    if (refreshTokenResponse.isSuccessful) {
                        val responseToken = json.decodeFromString(
                            refreshTokenResponse.body?.string().toString()
                        ) as BaseResponse<ResponseReIssueTokenDto>

                        if (responseToken.data != null) {
                            saveAccessToken(
                                responseToken.data.accessToken,
                                responseToken.data.refreshToken
                            )
                        }
                        refreshTokenResponse.close()
                        val newRequest = originalRequest.newAuthBuilder().build()
                        return chain.proceed(newRequest)
                    }
                    saveAccessToken("", "")
                } catch (t: Throwable) {
                    Timber.e(t)
                    saveAccessToken("", "")
                }
            }
        }
        return response
    }

    private fun Request.newAuthBuilder() =
        this.newBuilder().addHeader(HEADER_TOKEN, runBlocking(Dispatchers.IO) { getAccessToken() })

    private suspend fun getAccessToken(): String {
        return dataStoreRepository.getAccessToken().first() ?: ""
    }

    private suspend fun getRefreshToken(): String {
        return dataStoreRepository.getAccessToken().first() ?: ""
    }

    private fun saveAccessToken(accessToken: String, refreshToken: String) =
        runBlocking {
            dataStoreRepository.saveAccessToken(accessToken, refreshToken)
        }

    companion object {
        private const val HEADER_TOKEN = "accessToken"
        private const val CODE_TOKEN_EXPIRED = 401
        private const val REFRESH_TOKEN = "refreshToken"
    }
}
