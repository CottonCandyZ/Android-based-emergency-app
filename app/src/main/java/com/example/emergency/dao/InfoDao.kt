package com.example.emergency.dao

import androidx.room.*
import com.example.emergency.model.AbstractInfo
import com.example.emergency.model.Info
import com.example.emergency.model.InfoWithEmergencyContact

@Dao
interface InfoDao {
    // 插入替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInfo(vararg info: Info)

    // 更新全部
    @Update
    suspend fun updateInfo(vararg info: Info)

    // 更新部分
    @Update(entity = Info::class)
    suspend fun updateAbstractInfo(vararg abstractInfo: AbstractInfo)

    // 删除
    @Delete
    suspend fun delete(vararg info: Info)

    // 通过 ID 删除
    @Query("DELETE FROM personal_info WHERE id = :id")
    suspend fun deleteById(id: String)


    @Query("SELECT id, realName, phone, chosen FROM personal_info")
    suspend fun getAbstractInfo(): List<AbstractInfo>

    // 删除全部
    @Query("DELETE FROM personal_info")
    suspend fun nukeTable()

//    @ExperimentalCoroutinesApi
//    suspend fun getAbstractInfoDistinctUntilChanged() = getAbstractInfo().distinctUntilChanged()

    // 关联查询 查找紧急联系人
    @Transaction
    @Query("SELECT * FROM personal_info WHERE id = :id")
    suspend fun getInfoWithEmergencyContact(id: String): List<InfoWithEmergencyContact>

//    // 查询某行是否超时
//    @Query("SELECT id FROM personal_info WHERE lastUpdate < :remoteUpdate")
//    suspend fun getAbstractUpdateInfo(remoteUpdate: Long): List<Int>

//    @Query("SELECT MAX(id) FROM personal_info")
//    suspend fun getMaxId():Int
}