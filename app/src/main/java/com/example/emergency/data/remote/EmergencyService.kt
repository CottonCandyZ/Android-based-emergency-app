package com.example.emergency.data.remote

import cn.leancloud.AVInstallation
import cn.leancloud.AVObject
import cn.leancloud.AVPush
import cn.leancloud.AVUser
import com.example.emergency.data.entity.Call
import com.example.emergency.data.entity.Location
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.full.declaredMemberProperties


@Singleton
class EmergencyService @Inject constructor() {
    fun submitOneCall(call: Call): String {
        val submit = AVObject("Call")
        submit.put("callerAccountId", AVUser.getCurrentUser().objectId)
        submit.put("callerAccount", AVUser.getCurrentUser().mobilePhoneNumber)
        Call::class.declaredMemberProperties
            .forEach {
                submit.put(it.name, it.get(call))
            }
        var id: String? = null
        submit.saveInBackground().blockingSubscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: AVObject) {
                id = t.objectId
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        })
        val pushQuery = AVInstallation.getQuery()
        pushQuery.whereEqualTo("channels", "emergency")
        val push = AVPush()
        push.setQuery(pushQuery)
        push.setMessage("有新的呼救")
        push.setPushToAndroid(true)
        push.sendInBackground().blockingSubscribe()
        return id!!
    }

    fun submitLocation(callId: String, location: Location) {
        val submit = AVObject.createWithoutData("Call", callId)
        submit.put("locationName", location.name)
        submit.put("locationCoordinate", location.coordinate)
        submit.save()
    }

    fun setStatus(callId: String, status: String) {
        val submit = AVObject.createWithoutData("Call", callId)
        submit.put("status", status)
        submit.save()
    }
}