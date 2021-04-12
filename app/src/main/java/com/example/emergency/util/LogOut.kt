package com.example.emergency.util

import com.example.emergency.data.local.dao.InfoDao
import com.example.emergency.data.local.dao.UserDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogOut @Inject constructor(
    private val userDao: UserDao,
    private val infoDao: InfoDao
) {

    suspend fun clean() {
        userDao.nukeTable()
        infoDao.nukeTable()
    }

}