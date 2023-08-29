package com.go.sopt.winey.di

import android.content.Context
import com.go.sopt.winey.data.service.AuthService
import com.go.sopt.winey.data.service.FeedService
import com.go.sopt.winey.data.service.KakaoLoginService
import com.go.sopt.winey.data.service.NotificationService
import com.go.sopt.winey.data.service.RecommendService
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    private const val KAKAO_TALK_LOGIN = 0
    private const val KAKAO_ACCOUNT_LOGIN = 1

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideFeedService(retrofit: Retrofit): FeedService =
        retrofit.create(FeedService::class.java)

    @Provides
    @Singleton
    fun provideRecommendService(retrofit: Retrofit): RecommendService =
        retrofit.create(RecommendService::class.java)

    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationService =
        retrofit.create(NotificationService::class.java)

    @Provides
    fun provideKakaoLoginService(
        client: UserApiClient
    ): KakaoLoginService {
        return object : KakaoLoginService {
            override fun loginKakao(
                kakaoLoginCallBack: (OAuthToken?, Throwable?) -> Unit,
                context: Context
            ) {
                val kakaoLoginState =
                    if (client.isKakaoTalkLoginAvailable(context)) {
                        KAKAO_TALK_LOGIN
                    } else {
                        KAKAO_ACCOUNT_LOGIN
                    }

                when (kakaoLoginState) {
                    KAKAO_TALK_LOGIN -> {
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

            override fun logoutKakao(kakaoLogoutCallBack: (Throwable?) -> Unit) {
                client.logout(kakaoLogoutCallBack)
            }

            override fun deleteKakaoAccount(kakaoLogoutCallBack: (Throwable?) -> Unit) {
                client.unlink(kakaoLogoutCallBack)
            }
        }
    }
}
