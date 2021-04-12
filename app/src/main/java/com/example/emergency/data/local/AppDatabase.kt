package com.example.emergency.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.emergency.data.entity.Converters
import com.example.emergency.data.entity.EmergencyContact
import com.example.emergency.data.entity.Info
import com.example.emergency.data.entity.User
import com.example.emergency.data.local.dao.EmergencyContactDao
import com.example.emergency.data.local.dao.InfoDao
import com.example.emergency.data.local.dao.UserDao


@Database(
    entities = [Info::class, EmergencyContact::class, User::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun infoDao(): InfoDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun userDao(): UserDao
}