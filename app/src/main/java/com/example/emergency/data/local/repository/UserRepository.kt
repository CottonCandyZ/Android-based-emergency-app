package com.example.emergency.data.local.repository

import com.example.emergency.data.entity.User
import com.example.emergency.data.local.dao.UserDao
import com.example.emergency.data.remote.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val webService: WebService,
) {
    fun getUser(): Flow<List<User>> {
        return userDao.getUser()
    }

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val user = webService.getCurrentUser()
            if (user != null) {
                userDao.insertUser(user)
            }
        }
    }
}