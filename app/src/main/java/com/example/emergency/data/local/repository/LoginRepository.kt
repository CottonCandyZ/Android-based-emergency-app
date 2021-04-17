package com.example.emergency.data.local.repository

import com.example.emergency.data.remote.LoginService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginService: LoginService
) {
    suspend fun makeLoginRequest(phone: String, pwd: String) {
        withContext(Dispatchers.IO) {
            loginService.logIn(phone, pwd)
        }
    }

}