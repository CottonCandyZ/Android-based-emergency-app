package com.example.emergency.data

import com.example.emergency.WebService
import com.example.emergency.dao.EmergencyContactDao
import com.example.emergency.dao.InfoDao
import com.example.emergency.model.AbstractInfo
import com.example.emergency.model.EmergencyContact
import com.example.emergency.model.Info
import com.example.emergency.model.InfoWithEmergencyContact
import kotlinx.coroutines.ExperimentalCoroutinesApi

class InfoRepository(
    private val infoDao: InfoDao,
    private val emergencyContactDao: EmergencyContactDao,
    private val webService: WebService
) {
    @ExperimentalCoroutinesApi
    suspend fun getAbstractInfo(fetch: Boolean): List<AbstractInfo> {
        if (fetch) {
            refreshAbstractInfo()
        }
        return infoDao.getAbstractInfo()
    }


    private suspend fun refreshAbstractInfo() {
        val list = webService.getAbstractInfo()
        infoDao.nukeTable()
        infoDao.insertInfo(*list.toTypedArray())
    }

    suspend fun getInfo(id: String, fetch: Boolean): List<InfoWithEmergencyContact> {
        if (fetch) {
            refreshInfo(id)
        }
        return infoDao.getInfoWithEmergencyContact(id)
    }

    private suspend fun refreshInfo(id: String) {
        val result = webService.getInfoWithEmergencyContact(id)
        infoDao.updateInfo(result.info)
        result.emergencyContacts.forEach {
            emergencyContactDao.insertEmergencyContact(it)
        }
    }


    suspend fun saveInfo(info: Info): String {
        return webService.saveInfo(info)
    }

    suspend fun saveEmergencyContact(emergencyContact: EmergencyContact) {
        webService.saveEmergencyContact(emergencyContact)
    }


//    companion object {
//        val FRESH_TIMEOUT = TimeUnit.DAYS.toMillis(1)
//    }


}