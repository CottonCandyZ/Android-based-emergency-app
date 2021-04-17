package com.example.emergency.data.remote

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.example.emergency.data.entity.User
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WebService @Inject constructor() {
    fun getCurrentUser(): User? {
        val query = AVQuery<AVObject>("UserSignUp")
        val phone = AVUser.getCurrentUser().mobilePhoneNumber
        query.whereEqualTo("phone", phone)
        val result = query.find()
        var user: User? = null
        result.forEach {
            user = User(
                phone = phone,
                name = it.getString("name")
            )
        }
        return user
    }
}