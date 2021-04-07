package com.example.emergency.data.dao

import androidx.room.*
import com.example.emergency.data.entity.EmergencyContact


@Dao
interface EmergencyContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyContact(info: EmergencyContact)

    @Update
    suspend fun updateEmergencyContact(vararg info: EmergencyContact)

    @Delete
    suspend fun delete(vararg info: EmergencyContact)

    @Query("DELETE FROM emergency_contact WHERE infoId = :id")
    suspend fun deleteByInfoId(id: String)

    // 删除全部
    @Query("DELETE FROM emergency_contact")
    suspend fun nukeTable()

    /**
     * 查询在 [InfoDao] 中已写
     */
}