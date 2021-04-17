package com.example.emergency.data.local.repository

import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.livequery.AVLiveQueryEventHandler
import cn.leancloud.livequery.AVLiveQuerySubscribeCallback
import com.example.emergency.data.entity.AbstractInfo
import com.example.emergency.data.entity.Info
import com.example.emergency.data.entity.InfoWithEmergencyContact
import com.example.emergency.data.local.dao.EmergencyContactDao
import com.example.emergency.data.local.dao.InfoDao
import com.example.emergency.data.remote.InfoService
import com.example.emergency.util.convertAVObjectToEmergencyContact
import com.example.emergency.util.convertAVObjectToInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepository @Inject constructor(
    private val infoDao: InfoDao,
    private val emergencyContactDao: EmergencyContactDao,
    private val infoService: InfoService
) {
    private val infoAVLiveQueryEventHandler = object : AVLiveQueryEventHandler() {
        override fun onObjectCreated(avObject: AVObject) {
            super.onObjectCreated(avObject)
            MainScope().launch {
                updateInfo(avObject)
            }
        }

        override fun onObjectUpdated(avObject: AVObject, updateKeyList: MutableList<String>?) {
            super.onObjectUpdated(avObject, updateKeyList)
            MainScope().launch {
                updateInfo(avObject)
            }
        }

        override fun onObjectDeleted(objectId: String) {
            super.onObjectDeleted(objectId)
            MainScope().launch {
                infoDao.deleteById(objectId)
            }
        }
    }

    private val emergencyContactAVLiveQueryEventHandler = object : AVLiveQueryEventHandler() {
        override fun onObjectCreated(avObject: AVObject) {
            super.onObjectCreated(avObject)
            MainScope().launch {
                updateEmergencyContact(avObject)
            }
        }

        override fun onObjectUpdated(avObject: AVObject, updateKeyList: MutableList<String>?) {
            super.onObjectUpdated(avObject, updateKeyList)
            MainScope().launch {
                updateEmergencyContact(avObject)
            }
        }

        override fun onObjectDeleted(objectId: String) {
            super.onObjectDeleted(objectId)
            MainScope().launch {
                emergencyContactDao.deleteById(objectId)
            }
        }

    }

    init {
        infoService.infoLiveQuery.setEventHandler(infoAVLiveQueryEventHandler)
        infoService.emergencyLiveQuery.setEventHandler(emergencyContactAVLiveQueryEventHandler)
        infoService.infoLiveQuery.subscribeInBackground(object : AVLiveQuerySubscribeCallback() {
            override fun done(e: AVException?) {
                if (e == null) {
                    infoService.emergencyLiveQuery.subscribeInBackground(object :
                        AVLiveQuerySubscribeCallback() {
                        override fun done(e: AVException?) {
                        }
                    })
                }
            }

        })


//
//        infoService.autoFetchInfo(InfoAVLiveQueryEventHandler())
//        infoService.autoFetchEmergency( EmergencyContactAVLiveQueryEventHandler())
    }

    fun getAbstractInfo(): Flow<List<AbstractInfo>> {
        return infoDao.getAbstractInfo()
    }

    suspend fun getInfoWithEmergencyContact(infoId: String): List<InfoWithEmergencyContact> =
        withContext(Dispatchers.IO) {
            return@withContext infoDao.getInfoWithEmergencyContact(infoId)
        }


    private suspend fun updateInfo(remote: AVObject) {
        withContext(Dispatchers.IO) {
            infoDao.insertInfo(convertAVObjectToInfo(remote))
        }
    }

    private suspend fun updateEmergencyContact(remote: AVObject) {
        withContext(Dispatchers.IO) {
            emergencyContactDao.insertEmergencyContact(
                convertAVObjectToEmergencyContact(remote)
            )
        }
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
            infoWithEmergencyContact.emergencyContacts.forEach {
                infoService.deleteEmergencyContact(it.id)
            }
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