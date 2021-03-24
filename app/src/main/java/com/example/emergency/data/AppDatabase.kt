package com.example.emergency.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.emergency.dao.InfoDao
import com.example.emergency.model.EmergencyContact
import com.example.emergency.model.History
import com.example.emergency.model.Info

@Database(
    entities = [Info::class, EmergencyContact::class, History::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun infoDao(): InfoDao
    abstract fun emergencyContactDao(): EmergencyContact
    abstract fun historyDao(): History
}