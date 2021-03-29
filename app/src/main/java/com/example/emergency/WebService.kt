package com.example.emergency

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.example.emergency.model.Info
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.full.declaredMemberProperties

class WebService {
    suspend fun getAbstractInfo(): List<Info> = withContext(Dispatchers.IO) {
        val query = AVQuery<AVObject>("")
        query.selectKeys(listOf("userId", "id", "realName", "phone", "relationship, "))
        query.whereEqualTo("userId", AVUser.getCurrentUser().objectId)
        val result = query.find()
        val resultList: ArrayList<Info> = arrayListOf()
        result.forEach {
            val id = it.get("id") as Int
            val info = Info(
                id,
                realName = it.get("realName") as String,
                phone = it.get("phone") as String,
                relationship = it.get("relationship") as String
            )
            resultList.add(info)
        }
        return@withContext resultList
    }

    suspend fun saveInfo(info: Info) = withContext(Dispatchers.IO) {
        val remoteInfo = AVObject("Info")
        info.javaClass
            .kotlin.declaredMemberProperties
            .forEach {
                remoteInfo.put(it.name, it.get(info))
            }
    }


//    suspend fun saveInfo():List<AbstractInfo> {
//
//    }
}