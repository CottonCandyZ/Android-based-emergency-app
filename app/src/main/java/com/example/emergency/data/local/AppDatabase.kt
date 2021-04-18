package com.example.emergency.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.emergency.data.entity.*
import com.example.emergency.data.local.dao.EmergencyContactDao
import com.example.emergency.data.local.dao.HistoryDao
import com.example.emergency.data.local.dao.InfoDao
import com.example.emergency.data.local.dao.UserDao


@Database(
    entities = [Info::class, EmergencyContact::class, User::class, History::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun infoDao(): InfoDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun userDao(): UserDao
    abstract fun historyDao(): HistoryDao
}