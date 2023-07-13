package com.android.go.sopt.winey.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    /*
    @Singleton
    @Binds
    abstract fun providesExampleRepo(exampleRepository: ExampleRepositoryImpl): ExampleRepository = exampleRepository
    */
}