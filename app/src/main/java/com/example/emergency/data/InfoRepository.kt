package com.example.emergency.data

import com.example.emergency.WebService
import com.example.emergency.dao.EmergencyContactDao
import com.example.emergency.dao.InfoDao
import com.example.emergency.model.AbstractInfo
import com.example.emergency.model.EmergencyContact
import com.example.emergency.model.Info
import com.example.emergency.model.InfoWithEmergencyContact

class InfoRepository(
    private val infoDao: InfoDao,
    private val emergencyContactDao: EmergencyContactDao,
    private val webService: WebService
) {
    suspend fun getAbstractInfo(fetch: Boolean): List<AbstractInfo> {
        if (fetch) {
            refreshAbstractInfo()
        }
        return infoDao.getAbstractInfo()
    }


    private suspend fun refreshAbstractInfo() {
        val list = webService.getAbstractInfo()
        infoDao.nukeTable()
        emergencyContactDao.nukeTable()
        infoDao.insertInfo(*list.toTypedArray())
    }

    suspend fun getInfo(id: String, fetch: Boolean): List<InfoWithEmergencyContact>? {
        if (fetch) {
            if (!refreshInfo(id)) {
                return null
            }
        }
        return infoDao.getInfoWithEmergencyContact(id)
    }

    private suspend fun refreshInfo(id: String): Boolean {
        val result = webService.getInfoWithEmergencyContact(id) ?: return false
        infoDao.updateInfo(result.info)
        result.emergencyContacts.forEach {
            emergencyContactDao.insertEmergencyContact(it)
        }
        return true
    }


    suspend fun saveInfo(info: Info, saveById: Boolean): String {
        return webService.saveInfo(info, saveById)
    }

    suspend fun saveEmergencyContact(emergencyContact: EmergencyContact, saveById: Boolean) {
        webService.saveEmergencyContact(emergencyContact, saveById)
    }

    suspend fun deleteEmergencyContact(id: String) {
        webService.deleteEmergencyContact(id)
    }


//    companion object {
//        val FRESH_TIMEOUT = TimeUnit.DAYS.toMillis(1)
//    }


}