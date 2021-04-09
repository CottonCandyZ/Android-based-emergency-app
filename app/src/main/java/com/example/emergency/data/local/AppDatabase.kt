package com.example.emergency.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.emergency.data.dao.EmergencyContactDao
import com.example.emergency.data.dao.InfoDao
import com.example.emergency.data.entity.Converters
import com.example.emergency.data.entity.EmergencyContact
import com.example.emergency.data.entity.Info


@Database(
    entities = [Info::class, EmergencyContact::class], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun infoDao(): InfoDao
    abstract fun emergencyContactDao(): EmergencyContactDao
}