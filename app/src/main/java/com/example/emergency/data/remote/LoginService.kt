package com.example.emergency.data.remote

import cn.leancloud.AVUser
import javax.inject.Inject

class LoginService @Inject constructor() {
    fun logIn(phone: String, pwd: String) {
        AVUser.logIn(phone, pwd).blockingSubscribe()
    }
}