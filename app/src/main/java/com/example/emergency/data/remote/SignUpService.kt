package com.example.emergency.data.remote

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import cn.leancloud.sms.AVSMS
import cn.leancloud.sms.AVSMSOption
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpService @Inject constructor() {
    // 判断用户是否已注册
    fun judgeUserIfExist(phone: String): Boolean {
        val query = AVQuery<AVObject>("UserSignUp")
        query.whereEqualTo("phone", "+86$phone")
        return query.count() == 1
    }

    // 发送验证码
    fun sendCodeForSignUp(phone: String) {
        val option = AVSMSOption()
        // 未提供函数
        AVSMS.requestSMSCodeInBackground(
            "+86$phone",
            option
        ).blockingSubscribe()
    }

    // 检测登陆或注册验证码是否正确
    fun checkCodeToSignUpOrLogin(phone: String, code: String) {
        AVUser.signUpOrLoginByMobilePhone("+86$phone", code)
    }

    // 为用户设置密码
    fun setUserPassword(pwd: String) {
        val user = AVUser.getCurrentUser()
        user.password = pwd
        user.save()
        AVUser.logIn(user.mobilePhoneNumber, pwd).blockingSubscribe()
    }

    // 保存用户
    fun saveUser(phone: String, name: String) {
        val newUser = AVObject("UserSignUp")
        newUser.put("name", name)
        newUser.put("phone", "+86$phone")
        newUser.save()
    }

}