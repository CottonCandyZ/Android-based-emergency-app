package com.example.emergency.data.local

import com.example.emergency.data.Resource
import com.example.emergency.data.entity.User
import com.example.emergency.data.local.dao.UserDao
import com.example.emergency.data.remote.WebService
import com.example.emergency.util.USER_NOT_EXIST
import com.example.emergency.util.getErrorMessage
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val webService: WebService,
) {
    suspend fun getCurrentUser(): Resource<User> {
        val user: User?
        try {
            user = webService.getCurrentUser() ?: return Resource.Error(USER_NOT_EXIST)
        } catch (e: Exception) {
            return Resource.Error(getErrorMessage(e), userDao.getUserByPhone()[0])
        }
        userDao.insertUser(user)
        return Resource.Success(userDao.getUserByPhone()[0])
    }
}