package com.example.emergency.di

import android.content.Context
import androidx.room.Room
import com.example.emergency.data.local.AppDatabase
import com.example.emergency.data.local.dao.EmergencyContactDao
import com.example.emergency.data.local.dao.HistoryDao
import com.example.emergency.data.local.dao.InfoDao
import com.example.emergency.data.local.dao.UserDao
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
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
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