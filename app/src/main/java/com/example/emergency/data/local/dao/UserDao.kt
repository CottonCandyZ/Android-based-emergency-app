package com.example.emergency.data.local.dao

import androidx.room.*
import com.example.emergency.data.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg user: User)

    @Update
    suspend fun updateUser(vararg user: User)

    @Delete
    suspend fun deleteUser(vararg user: User)

    @Query("SELECT * FROM user")
    suspend fun getUserByPhone(): List<User>

    // 删除全部
    @Query("DELETE FROM user")
    suspend fun nukeTable()
}