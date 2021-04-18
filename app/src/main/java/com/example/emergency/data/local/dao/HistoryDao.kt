package com.example.emergency.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.emergency.data.entity.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(vararg history: History)

    @Query("DELETE FROM history")
    fun nukeTable()

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryById(id: String)

    @Query("SELECT * FROM history ORDER BY ID DESC")
    fun getHistory(): Flow<List<History>>

}