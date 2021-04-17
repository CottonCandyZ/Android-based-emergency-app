package com.example.emergency.data.remote

import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.example.emergency.data.entity.Call
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.full.declaredMemberProperties


@Singleton
class EmergencyService @Inject constructor() {
    fun submitOneCall(call: Call) {
        val submit = AVObject("Call")
        submit.put("callerAccountId", AVUser.getCurrentUser().objectId)
        submit.put("callerAccount", AVUser.getCurrentUser().mobilePhoneNumber)
        Call::class.declaredMemberProperties
            .forEach {
                submit.put(it.name, it.get(call))
            }
        submit.save()
    }
}