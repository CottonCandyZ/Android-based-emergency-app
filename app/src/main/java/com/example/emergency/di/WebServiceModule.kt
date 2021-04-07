package com.example.emergency.di

import com.example.emergency.data.remote.WebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WebServiceModule {
    @Provides
    @Singleton
    fun provideWebService(): WebService {
        return WebService()
    }
}