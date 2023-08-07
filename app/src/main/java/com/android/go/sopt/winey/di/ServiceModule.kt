package com.android.go.sopt.winey.di

import android.content.Context
import com.android.go.sopt.winey.data.service.AuthService
import com.android.go.sopt.winey.data.service.KakaoLoginService
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    const val KAKAO_APP_LOGIN = 0
    const val KAKAO_ACCOUNT_LOGIN = 1

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    fun provideKakaoLoginService(
        @ApplicationContext context: Context,
        client: UserApiClient
    ): KakaoLoginService {
        return object : KakaoLoginService {
            override fun startKakaoLogin(kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit) {
                val kakaoLoginState =
                    if (client.isKakaoTalkLoginAvailable(context)) {
                        KAKAO_APP_LOGIN
                    } else {
                        KAKAO_ACCOUNT_LOGIN
                    }

                when (kakaoLoginState) {
                    KAKAO_APP_LOGIN -> {
                        client.loginWithKakaoTalk(
                            context,
                            callback = kakaoLoginCallBack
                        )
                    }

                    KAKAO_ACCOUNT_LOGIN -> {
                        client.loginWithKakaoAccount(
                            context,
                            callback = kakaoLoginCallBack
                        )
                    }
                }
            }

            override fun kakaoLogout(kakaoLogoutCallBack: (Throwable?) -> Unit) {
                client.logout(kakaoLogoutCallBack)
            }

            override fun kakaoDeleteAccount(kakaoLogoutCallBack: (Throwable?) -> Unit) {
                client.unlink(kakaoLogoutCallBack)
            }
        }
    }
}
