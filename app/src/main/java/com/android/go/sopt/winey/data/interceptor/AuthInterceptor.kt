package com.android.go.sopt.winey.data.interceptor

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class AuthInterceptor @Inject constructor(
    @ApplicationContext context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val headerRequest = originalRequest.newBuilder()
            .addHeader(HEADER_TOKEN, USER_ID)
            .build()

        return chain.proceed(headerRequest)
    }

    companion object {
        private const val HEADER_TOKEN = "userId"
        const val USER_ID = "24"
    }
}