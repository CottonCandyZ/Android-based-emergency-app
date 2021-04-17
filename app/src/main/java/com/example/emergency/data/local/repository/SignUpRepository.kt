package com.example.emergency.data.local.repository

import com.example.emergency.data.remote.SignUpService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpRepository @Inject constructor(
    private val signUpService: SignUpService
) {

    suspend fun judgeUserIfExist(phone: String): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext signUpService.judgeUserIfExist(phone)
        }

    suspend fun sendCodeForSignUp(phone: String) {
        withContext(Dispatchers.IO) {
            signUpService.sendCodeForSignUp(phone)
        }
    }

    suspend fun checkCodeToSignUpOrLogin(phone: String, code: String) {
        withContext(Dispatchers.IO) {
            signUpService.checkCodeToSignUpOrLogin(phone, code)
        }
    }

    suspend fun saveUser(phone: String, userName: String, pwd: String) {
        withContext(Dispatchers.IO) {
            signUpService.saveUser(phone, userName)
            signUpService.setUserPassword(pwd)
        }
    }
}