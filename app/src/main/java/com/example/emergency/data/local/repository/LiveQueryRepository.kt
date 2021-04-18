package com.example.emergency.data.local.repository

import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.livequery.AVLiveQueryEventHandler
import cn.leancloud.livequery.AVLiveQuerySubscribeCallback
import com.example.emergency.data.local.dao.EmergencyContactDao
import com.example.emergency.data.local.dao.HistoryDao
import com.example.emergency.data.local.dao.InfoDao
import com.example.emergency.data.remote.HistoryService
import com.example.emergency.data.remote.InfoService
import com.example.emergency.util.convertAVObjectToEmergencyContact
import com.example.emergency.util.convertAVObjectToHistory
import com.example.emergency.util.convertAVObjectToInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveQueryRepository @Inject constructor(
    private val historyService: HistoryService,
    private val infoService: InfoService,
    private val historyDao: HistoryDao,
    private val infoDao: InfoDao,
    private val emergencyContactDao: EmergencyContactDao
) {

    private val historyAVLiveQueryEventHandler = object : AVLiveQueryEventHandler() {
        override fun onObjectCreated(avObject: AVObject) {
            super.onObjectCreated(avObject)
            MainScope().launch {
                updateHistory(avObject)
            }

        }

        override fun onObjectUpdated(avObject: AVObject, updateKeyList: MutableList<String>?) {
            super.onObjectUpdated(avObject, updateKeyList)
            MainScope().launch {
                updateHistory(avObject)
            }
        }

        override fun onObjectDeleted(objectId: String) {
            super.onObjectDeleted(objectId)
            MainScope().launch {
                historyDao.deleteHistoryById(objectId)
            }
        }
    }
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

    fun init() {
        infoService.infoLiveQuery.setEventHandler(infoAVLiveQueryEventHandler)
        infoService.emergencyLiveQuery.setEventHandler(emergencyContactAVLiveQueryEventHandler)
        historyService.historyLiveQuery.setEventHandler(historyAVLiveQueryEventHandler)
        infoService.infoLiveQuery.subscribeInBackground(object : AVLiveQuerySubscribeCallback() {
            override fun done(e: AVException?) {
                if (e == null) {
                    infoService.emergencyLiveQuery.subscribeInBackground(object :
                        AVLiveQuerySubscribeCallback() {
                        override fun done(e: AVException?) {
                            if (e == null) {
                                historyService.historyLiveQuery.subscribeInBackground(object :
                                    AVLiveQuerySubscribeCallback() {
                                    override fun done(e: AVException?) {

                                    }
                                })
                            }
                        }
                    })
                }
            }
        })
    }

    fun unsubscribe() {
        infoService.infoLiveQuery.unsubscribeInBackground(object : AVLiveQuerySubscribeCallback() {
            override fun done(e: AVException?) {
                infoService.emergencyLiveQuery.unsubscribeInBackground(object :
                    AVLiveQuerySubscribeCallback() {
                    override fun done(e: AVException?) {
                        if (e == null) {
                            historyService.historyLiveQuery.unsubscribeInBackground(object :
                                AVLiveQuerySubscribeCallback() {
                                override fun done(e: AVException?) {

                                }
                            })
                        }
                    }
                })
            }

        })
    }

    private suspend fun updateHistory(remote: AVObject) {
        withContext(Dispatchers.IO) {
            historyDao.insertHistory(
                convertAVObjectToHistory(remote)
            )
        }
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
}