package com.example.emergency.data.local.repository

import com.example.emergency.data.Resource
import com.example.emergency.data.entity.AbstractInfo
import com.example.emergency.data.entity.Call
import com.example.emergency.data.entity.InfoWithEmergencyContact
import com.example.emergency.data.local.dao.EmergencyContactDao
import com.example.emergency.data.local.dao.InfoDao
import com.example.emergency.data.remote.WebService
import com.example.emergency.util.ID_NOT_FOUND_ERROR
import com.example.emergency.util.getErrorMessage
import javax.inject.Inject

class InfoRepository @Inject constructor(
    private val infoDao: InfoDao,
    private val emergencyContactDao: EmergencyContactDao,
    private val webService: WebService
) {
    suspend fun getAbstractInfo(fetch: Boolean): Resource<List<AbstractInfo>> {
        if (fetch) {
            try {
                refreshAbstractInfo()
            } catch (e: Exception) {
                return Resource.Error(getErrorMessage(e), infoDao.getAbstractInfo())
            }
        }
        return Resource.Success(infoDao.getAbstractInfo())
    }


    private suspend fun refreshAbstractInfo() {
        val list = webService.getAbstractInfo()
        infoDao.nukeTable()
        emergencyContactDao.nukeTable()
        infoDao.insertInfo(*list.toTypedArray())
    }

    suspend fun getInfo(id: String): Resource<List<InfoWithEmergencyContact>> {
        try {
            if (!refreshInfo(id)) {
                return Resource.Error(ID_NOT_FOUND_ERROR)
            }
        } catch (e: Exception) {
            return Resource.Error(
                getErrorMessage(e),
                infoDao.getInfoWithEmergencyContact(id)
            )
        }
        return Resource.Success(infoDao.getInfoWithEmergencyContact(id))
    }

    private suspend fun refreshInfo(id: String): Boolean {
        val result = webService.getInfoWithEmergencyContact(id) ?: return false
        infoDao.updateInfo(result.info)
        result.emergencyContacts.forEach {
            emergencyContactDao.insertEmergencyContact(it)
        }
        return true
    }

    suspend fun deleteEmergencyContact(id: String) {
        webService.deleteEmergencyContact(id)
    }


    suspend fun saveInfoWithEmergencyContact(
        infoWithEmergencyContact: InfoWithEmergencyContact,
        saveById: Boolean
    ): String {
        val infoId = webService.saveInfo(infoWithEmergencyContact.info, saveById)
        infoWithEmergencyContact.emergencyContacts.forEach {
            it.infoId = infoId
            webService.saveEmergencyContact(it, saveById)
        }
        return infoId
    }

    suspend fun deleteInfoWithEmergencyContact(infoWithEmergencyContact: InfoWithEmergencyContact) {
        webService.deleteInfo(infoWithEmergencyContact.info.id)
        infoWithEmergencyContact.emergencyContacts.forEach {
            webService.deleteEmergencyContact(it.id)
            emergencyContactDao.deleteByInfoId(it.id)
        }
        infoDao.deleteById(infoWithEmergencyContact.info.id)
    }

    suspend fun updateItemChosen(remove: AbstractInfo, update: AbstractInfo): Boolean {
        try {
            webService.updateInfoChosen(remove.id, update.id)
        } catch (e: Exception) {
            return false
        }
//        infoDao.updateAbstractInfo(remove)
//        infoDao.updateAbstractInfo(update)
        return true
    }

    suspend fun submitOneCall(call: Call) {
        webService.submitOneCall(call)
    }


//    companion object {
//        val FRESH_TIMEOUT = TimeUnit.DAYS.toMillis(1)
//    }
}