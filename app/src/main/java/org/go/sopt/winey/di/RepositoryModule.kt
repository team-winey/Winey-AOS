package org.go.sopt.winey.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.go.sopt.winey.data.repository.AuthRepositoryImpl
import org.go.sopt.winey.data.repository.DataStoreRepositoryImpl
import org.go.sopt.winey.data.repository.FeedRepositoryImpl
import org.go.sopt.winey.data.repository.KakaoLoginRepositoryImpl
import org.go.sopt.winey.data.repository.NotificationRepositoryImpl
import org.go.sopt.winey.data.repository.RecommendRepositoryImpl
import org.go.sopt.winey.domain.repository.AuthRepository
import org.go.sopt.winey.domain.repository.DataStoreRepository
import org.go.sopt.winey.domain.repository.FeedRepository
import org.go.sopt.winey.domain.repository.KakaoLoginRepository
import org.go.sopt.winey.domain.repository.NotificationRepository
import org.go.sopt.winey.domain.repository.RecommendRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindsAuthRepository(
        authRepository: AuthRepositoryImpl
    ): AuthRepository

    @Singleton
    @Binds
    abstract fun bindsFeedRepository(
        feedRepository: FeedRepositoryImpl
    ): FeedRepository

    @Singleton
    @Binds
    abstract fun bindsRecommendRepository(
        recommendRepository: RecommendRepositoryImpl
    ): RecommendRepository

    @Singleton
    @Binds
    abstract fun bindsKakaoLoginRepository(
        kakaoLoginRepository: KakaoLoginRepositoryImpl
    ): KakaoLoginRepository

    @Singleton
    @Binds
    abstract fun bindsDataStoreRepository(
        dataStoreRepository: DataStoreRepositoryImpl
    ): DataStoreRepository

    @Singleton
    @Binds
    abstract fun bindsNotificationRepository(
        notificationRepository: NotificationRepositoryImpl
    ): NotificationRepository
}
