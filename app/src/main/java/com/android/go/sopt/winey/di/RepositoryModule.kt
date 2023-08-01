package com.android.go.sopt.winey.di

import com.android.go.sopt.winey.data.repository.AuthRepositoryImpl
import com.android.go.sopt.winey.domain.repository.AuthRepository
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
}
