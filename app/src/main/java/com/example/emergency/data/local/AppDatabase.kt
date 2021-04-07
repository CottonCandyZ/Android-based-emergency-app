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
//    abstract fun historyDao(): History


//    companion object {
//        @Volatile
//        private var instance: AppDatabase? = null
//
//        fun getInstance(
//            context: Context
//        ): AppDatabase = instance ?: synchronized(this) {
//            instance ?: buildDatabase(context).also { instance = it }
//        }
//
//        private fun buildDatabase(context: Context): AppDatabase {
//            return Room.databaseBuilder(
//                context,
//                AppDatabase::class.java,
//                "app_database"
//            ).build()
//        }
//    }
}