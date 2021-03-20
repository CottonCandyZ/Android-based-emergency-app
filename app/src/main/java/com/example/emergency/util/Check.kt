package com.example.emergency.util

import android.content.Context
import android.widget.Toast
import cn.leancloud.AVException

fun showError(e: Throwable, context: Context) {
    val message = when (val code = AVException(e).code) {
        210 -> "用户名和密码不匹配"
        211 -> "该用户尚未注册"
        219 -> "登录失败次数超过限制，请稍候再试，或尝试重制密码"
        603 -> "验证码无效"
        else -> "error: code $code"
    }
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}