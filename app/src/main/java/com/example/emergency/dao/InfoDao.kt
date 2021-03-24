package com.example.emergency.dao

import androidx.room.*
import com.example.emergency.model.Info
import com.example.emergency.model.InfoWithEmergencyContact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface InfoDao {
    @Insert
    suspend fun insertInfo(info: Info)

    @Update
    suspend fun updateInfo(vararg info: Info)

    @Delete
    suspend fun delete(vararg info: Info)

    @Query("SELECT real_name, phone, relationship FROM personal_info")
    suspend fun getAbstractInfo(): Flow<List<Info>>

    @ExperimentalCoroutinesApi
    suspend fun getAbstractInfoDistinctUntilChanged() = getAbstractInfo().distinctUntilChanged()

    // 关联查询 查找紧急联系人
    @Transaction
    @Query("SELECT * FROM personal_info WHERE id = :id")
    suspend fun getInfoWithEmergencyContact(id: Int): Flow<List<InfoWithEmergencyContact>>

}