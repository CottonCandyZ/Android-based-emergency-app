package com.example.emergency.data.local.dao

import androidx.room.*
import com.example.emergency.data.entity.AbstractInfo
import com.example.emergency.data.entity.Info
import com.example.emergency.data.entity.InfoWithEmergencyContact
import kotlinx.coroutines.flow.Flow

@Dao
interface InfoDao {
    // 插入替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInfo(vararg info: Info)

    @Update
    suspend fun updateInfo(vararg info: Info)

    // 通过 ID 删除
    @Query("DELETE FROM personal_info WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT id, realName, phone, chosen FROM personal_info ORDER BY ID DESC")
    fun getAbstractInfo(): Flow<List<AbstractInfo>>

    // 删除全部
    @Query("DELETE FROM personal_info")
    fun nukeTable()

    // 关联查询 查找紧急联系人
    @Transaction
    @Query("SELECT * FROM personal_info WHERE id = :id")
    fun getInfoWithEmergencyContact(id: String): List<InfoWithEmergencyContact>

    @Query("SELECT COUNT(*) FROM personal_info")
    fun getInfoNumber(): Int

    @Query("SELECT * FROM personal_info WHERE chosen == 1")
    fun getCurrentChosen(): Flow<List<Info>>

    @Query("SELECT id FROM personal_info WHERE chosen != 1")
    fun getNotChosen(): List<String>


//    // 查询某行是否超时
//    @Query("SELECT id FROM personal_info WHERE lastUpdate < :remoteUpdate")
//    suspend fun getAbstractUpdateInfo(remoteUpdate: Long): List<Int>

//    @Query("SELECT MAX(id) FROM personal_info")
//    suspend fun getMaxId():Int
}