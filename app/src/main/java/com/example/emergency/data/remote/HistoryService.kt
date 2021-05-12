package com.example.emergency.data.remote

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.example.emergency.data.entity.History
import com.example.emergency.util.convertAVObjectToHistory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryService @Inject constructor() {
    fun getHistory(): List<History> {
        val query = AVQuery<AVObject>("Call")
        query.whereEqualTo("callerAccountId", AVUser.getCurrentUser().objectId)
        val infoResult = query.find()
        val resultList: ArrayList<History> = arrayListOf()
        infoResult.forEach {
            resultList.add(convertAVObjectToHistory(it))
        }
        return resultList
    }
}