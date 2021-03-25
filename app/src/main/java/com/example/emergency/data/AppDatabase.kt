package com.example.emergency.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.emergency.dao.EmergencyContactDao
import com.example.emergency.dao.InfoDao
import com.example.emergency.model.Converters
import com.example.emergency.model.EmergencyContact
import com.example.emergency.model.Info

@Database(
    entities = [Info::class, EmergencyContact::class], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun infoDao(): InfoDao
    abstract fun emergencyContactDao(): EmergencyContactDao
//    abstract fun historyDao(): History

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(
            context: Context
        ): AppDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            ).build()
        }
    }
}