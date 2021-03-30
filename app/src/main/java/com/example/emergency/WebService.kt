package com.example.emergency

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.example.emergency.model.EmergencyContact
import com.example.emergency.model.Info
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.full.declaredMemberProperties

class WebService {
    suspend fun getAbstractInfo(): List<Info> = withContext(Dispatchers.IO) {
        val query = AVQuery<AVObject>("")
        query.selectKeys(listOf("userId", "id", "realName", "phone"))
        query.whereEqualTo("userId", AVUser.getCurrentUser().objectId)
        val result = query.find()
        val resultList: ArrayList<Info> = arrayListOf()
        result.forEach {
            val id = it.get("id") as Int
            val info = Info(
                id,
                realName = it.get("realName") as String,
                phone = it.get("phone") as String,
            )
            resultList.add(info)
        }
        return@withContext resultList
    }

    suspend fun saveInfo(info: Info): Int =
        withContext(Dispatchers.IO) {
            val remoteInfo = AVObject("Info")
            remoteInfo.put("userId", AVUser.getCurrentUser().objectId)
            info.javaClass
                .kotlin.declaredMemberProperties
                .forEach {
                    remoteInfo.put(it.name, it.get(info))
                }
            var id = 0
            remoteInfo.saveInBackground().blockingSubscribe(object : Observer<AVObject> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: AVObject) {
                    id = t.get("id") as Int
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
            return@withContext id
        }

    suspend fun saveEmergencyContact(emergencyContact: EmergencyContact) =
        withContext(Dispatchers.IO) {
            val remoteEmergencyContact = AVObject("EmergencyContact")
            emergencyContact.javaClass
                .kotlin.declaredMemberProperties
                .forEach {
                    remoteEmergencyContact.put(it.name, it.get(emergencyContact))
                }
            remoteEmergencyContact.save()
        }


//    suspend fun saveInfo():List<AbstractInfo> {
//
//    }
}