package com.example.emergency.data.local.repository

import com.example.emergency.data.entity.AbstractInfo
import com.example.emergency.data.entity.Info
import com.example.emergency.data.entity.InfoWithEmergencyContact
import com.example.emergency.data.local.dao.EmergencyContactDao
import com.example.emergency.data.local.dao.InfoDao
import com.example.emergency.data.remote.InfoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InfoRepository @Inject constructor(
    private val infoDao: InfoDao,
    private val emergencyContactDao: EmergencyContactDao,
    private val infoService: InfoService
) {
    fun getAbstractInfo(): Flow<List<AbstractInfo>> {
        return infoDao.getAbstractInfo()
    }

    suspend fun getInfoWithEmergencyContact(infoId: String): List<InfoWithEmergencyContact> =
        withContext(Dispatchers.IO) {
            return@withContext infoDao.getInfoWithEmergencyContact(infoId)
        }



    suspend fun getInfoNumber(): Int =
        withContext(Dispatchers.IO) {
            return@withContext infoDao.getInfoNumber()
        }

    suspend fun refreshInfo() {
        withContext(Dispatchers.IO) {
            val list = infoService.getInfo()
            infoDao.nukeTable()
            infoDao.insertInfo(*list.toTypedArray())
        }
        withContext(Dispatchers.IO) {
            val list = infoService.getEmergencyContact()
            emergencyContactDao.nukeTable()
            emergencyContactDao.insertEmergencyContact(*list.toTypedArray())
        }
    }

    suspend fun deleteEmergencyContact(id: String) {
        withContext(Dispatchers.IO) {
            infoService.deleteEmergencyContact(id)
        }

    }

    suspend fun saveInfoWithEmergencyContact(
        infoWithEmergencyContact: InfoWithEmergencyContact,
        saveById: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val infoId = infoService.saveInfo(infoWithEmergencyContact.info, saveById)
            infoWithEmergencyContact.emergencyContacts.forEach {
                it.infoId = infoId
                infoService.saveEmergencyContact(it, saveById)
            }

        }
    }

    suspend fun deleteInfoWithEmergencyContact(infoWithEmergencyContact: InfoWithEmergencyContact) {
        withContext(Dispatchers.IO) {
            infoService.deleteInfo(infoWithEmergencyContact.info.id)
        }

    }


    suspend fun updateItemChosen(removeId: String, updateId: String) {
        withContext(Dispatchers.IO) {
            infoService.updateInfoChosen(removeId, updateId)
        }
    }

    suspend fun getNotChosen(): List<String> =
        withContext(Dispatchers.IO) {
            return@withContext infoDao.getNotChosen()
        }

    fun getCurrentChosen(): Flow<List<Info>> {
        return infoDao.getCurrentChosen()
    }


    suspend fun updateInfoChosenWithOutRemove(updateId: String) {
        withContext(Dispatchers.IO) {
            infoService.updateInfoChosenWithOutRemove(updateId)
        }
    }
}