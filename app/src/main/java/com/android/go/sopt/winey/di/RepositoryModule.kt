package com.android.go.sopt.winey.di

import com.android.go.sopt.winey.data.repository.AuthRepositoryImpl
import com.android.go.sopt.winey.data.repository.DataStoreRepositoryImpl
import com.android.go.sopt.winey.data.repository.FeedRepositoryImpl
import com.android.go.sopt.winey.data.repository.KakaoLoginRepositoryImpl
import com.android.go.sopt.winey.data.repository.RecommendRepositoryImpl
import com.android.go.sopt.winey.domain.repository.AuthRepository
import com.android.go.sopt.winey.domain.repository.DataStoreRepository
import com.android.go.sopt.winey.domain.repository.FeedRepository
import com.android.go.sopt.winey.domain.repository.KakaoLoginRepository
import com.android.go.sopt.winey.domain.repository.RecommendRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
}
