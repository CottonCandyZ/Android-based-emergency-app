package com.example.emergency.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.emergency.data.entity.EmergencyContact


@Dao
interface EmergencyContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEmergencyContact(vararg emergencyContact: EmergencyContact)

    @Query("DELETE FROM emergency_contact WHERE id = :id")
    suspend fun deleteById(id: String)

    // 删除全部
    @Query("DELETE FROM emergency_contact")
    fun nukeTable()

    /**
     * 查询在 [InfoDao] 中已写
     */
}