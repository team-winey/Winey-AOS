package com.go.sopt.winey.di

import android.app.Application
import android.content.Context
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.go.sopt.winey.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AmplitudeModule {
    @Provides
    @Singleton
    fun provideAmplitudeClient(
        @ApplicationContext context: Context,
        application: Application
    ): AmplitudeClient = Amplitude.getInstance()
        .initialize(context, BuildConfig.AMPLITUDE_API_KEY)
        .enableForegroundTracking(application)
}
