package com.example.emergency.util

import com.example.emergency.data.local.dao.EmergencyContactDao
import com.example.emergency.data.local.dao.InfoDao
import com.example.emergency.data.local.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogOut @Inject constructor(
    private val userDao: UserDao,
    private val infoDao: InfoDao,
    private val emergencyContactDao: EmergencyContactDao
) {

    suspend fun clean() {
        withContext(Dispatchers.IO) {
            userDao.nukeTable()
            infoDao.nukeTable()
            emergencyContactDao.nukeTable()
        }
    }

}