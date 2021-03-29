package com.example.emergency.data

import com.example.emergency.WebService
import com.example.emergency.dao.InfoDao
import com.example.emergency.model.AbstractInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit

class InfoRepository(private val infoDao: InfoDao, private val webService: WebService) {
    @ExperimentalCoroutinesApi
    suspend fun getAbstractInfo(fetch: Boolean): List<AbstractInfo> {
        if (fetch) {
            refreshAbstractInfo()
        }
        return infoDao.getAbstractInfo()
    }


    private suspend fun refreshAbstractInfo() {
//        val list = infoDao.getAbstractInfo()
//        val remoteList = webService.getAbstractInfo()
//        list.collect {
//            it.forEach { info ->
//                val remoteInfo = remoteList[info.id]
//                if (remoteInfo == null) {
//                    infoDao.deleteById(info.id)
//                } else if (info.lastUpdate < remoteInfo.id) {
//                    infoDao.updateAbstractInfo(remoteInfo)
//                }
//            }
//        }

        val list = webService.getAbstractInfo()
        infoDao.insertInfo(*list.toTypedArray())
    }


    companion object {
        val FRESH_TIMEOUT = TimeUnit.DAYS.toMillis(1)
    }


}