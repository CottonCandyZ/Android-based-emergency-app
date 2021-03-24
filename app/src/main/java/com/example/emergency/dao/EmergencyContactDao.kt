package com.example.emergency.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.example.emergency.model.EmergencyContact


@Dao
interface EmergencyContactDao {
    @Insert
    suspend fun insertEmergencyContact(info: EmergencyContact)

    @Update
    suspend fun updateEmergencyContact(vararg info: EmergencyContact)

    @Delete
    suspend fun delete(vararg info: EmergencyContact)

    /**
     * 查询在 [InfoDao] 中已写
     */
}