package com.android.go.sopt.winey.data.interceptor

import android.content.Context
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    @ApplicationContext context: Context,
    private val dataStoreRepository: DataStoreRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { getAccessToken() }

        val originalRequest = chain.request()

        val headerRequest = originalRequest.newBuilder()
            .addHeader(HEADER_TOKEN, accessToken)
            .build()

        return chain.proceed(headerRequest)
    }

    private suspend fun getAccessToken(): String {
        return dataStoreRepository.getAccessToken().first() ?: ""
    }

    companion object {
        private const val HEADER_TOKEN = "accessToken"
        const val USER_ID = "1"
    }
}
