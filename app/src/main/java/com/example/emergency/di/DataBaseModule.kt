package com.example.emergency.di

import android.content.Context
import androidx.room.Room
import com.example.emergency.data.dao.EmergencyContactDao
import com.example.emergency.data.dao.InfoDao
import com.example.emergency.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    fun provideInfoDao(appDatabase: AppDatabase): InfoDao {
        return appDatabase.infoDao()
    }

    @Provides
    fun provideEmergencyDao(appDatabase: AppDatabase): EmergencyContactDao {
        return appDatabase.emergencyContactDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }
}