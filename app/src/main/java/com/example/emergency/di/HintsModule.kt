package com.example.emergency.di

import android.content.Context
import com.example.emergency.util.Hints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class HintsModule {
    @Provides
    @Singleton
    fun provideHints(@ApplicationContext context: Context): Hints {
        return Hints(context)
    }
}