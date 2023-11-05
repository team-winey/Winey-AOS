package org.go.sopt.winey.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.go.sopt.winey.data.service.AuthService
import org.go.sopt.winey.data.service.FeedService
import org.go.sopt.winey.data.service.NotificationService
import org.go.sopt.winey.data.service.RecommendService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
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
}
