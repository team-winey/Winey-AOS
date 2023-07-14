package com.android.go.sopt.winey.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    companion object {
        private const val HEADER_TOKEN = "userId"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val headerRequest = originalRequest.newBuilder()
            .addHeader(HEADER_TOKEN, "1")
            .build()

        return chain.proceed(headerRequest)
    }
}