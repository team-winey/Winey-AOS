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
        private const val HEADER_TOKEN = "accessToken"
        const val USER_ID = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0b2tlbiIsImlhdCI6MTY5MTA2MjY5NCwiZXhwIjoxNjkxMzIxODk0LCJ1c2VySWQiOiIzIn0.5jVUNzMefXca4buVsFDlqGPe3PsecpbIj4Yk-3NZs001JOzNA3OQgI5h7kv30AjB"
    }
}
