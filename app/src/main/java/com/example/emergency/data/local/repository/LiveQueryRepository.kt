package com.example.emergency.data.local.repository

import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import cn.leancloud.livequery.AVLiveQuery
import cn.leancloud.livequery.AVLiveQueryEventHandler
import cn.leancloud.livequery.AVLiveQuerySubscribeCallback
import com.example.emergency.data.local.dao.EmergencyContactDao
import com.example.emergency.data.local.dao.HistoryDao
import com.example.emergency.data.local.dao.InfoDao
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
    private val historyDao: HistoryDao,
    private val infoDao: InfoDao,
    private val emergencyContactDao: EmergencyContactDao
) {
    private lateinit var infoLiveQuery: AVLiveQuery
    private lateinit var emergencyLiveQuery: AVLiveQuery
    private lateinit var historyLiveQuery: AVLiveQuery

//    private val historyAVLiveQueryEventHandler = object : AVLiveQueryEventHandler() {
//        override fun onObjectCreated(avObject: AVObject) {
//            super.onObjectCreated(avObject)
//            MainScope().launch {
//                updateHistory(avObject)
//            }
//
//        }
//
//        override fun onObjectUpdated(avObject: AVObject, updateKeyList: MutableList<String>?) {
//            super.onObjectUpdated(avObject, updateKeyList)
//            MainScope().launch {
//                updateHistory(avObject)
//            }
//        }
//
//        override fun onObjectDeleted(objectId: String) {
//            super.onObjectDeleted(objectId)
//            MainScope().launch {
//                historyDao.deleteHistoryById(objectId)
//            }
//        }
//    }
//    private val infoAVLiveQueryEventHandler = object : AVLiveQueryEventHandler() {
//        override fun onObjectCreated(avObject: AVObject) {
//            super.onObjectCreated(avObject)
//            MainScope().launch {
//                updateInfo(avObject)
//            }
//        }
//
//        override fun onObjectUpdated(avObject: AVObject, updateKeyList: MutableList<String>?) {
//            super.onObjectUpdated(avObject, updateKeyList)
//            MainScope().launch {
//                updateInfo(avObject)
//            }
//        }
//
//        override fun onObjectLeave(avObject: AVObject, updateKeyList: MutableList<String>?) {
//            super.onObjectLeave(avObject, updateKeyList)
//            MainScope().launch {
//                infoDao.deleteById(avObject.objectId)
//            }
//        }
//
//        override fun onObjectDeleted(objectId: String) {
//            super.onObjectDeleted(objectId)
//            MainScope().launch {
//                infoDao.deleteById(objectId)
//            }
//        }
//    }

//    private val emergencyContactAVLiveQueryEventHandler = object : AVLiveQueryEventHandler() {
//        override fun onObjectCreated(avObject: AVObject) {
//            super.onObjectCreated(avObject)
//            MainScope().launch {
//                updateEmergencyContact(avObject)
//            }
//        }
//
//        override fun onObjectUpdated(avObject: AVObject, updateKeyList: MutableList<String>?) {
//            super.onObjectUpdated(avObject, updateKeyList)
//            MainScope().launch {
//                updateEmergencyContact(avObject)
//            }
//        }
//
//        override fun onObjectDeleted(objectId: String) {
//            super.onObjectDeleted(objectId)
//            MainScope().launch {
//                emergencyContactDao.deleteById(objectId)
//            }
//        }
//
//    }

    fun init() {
        var query = AVQuery<AVObject>("Info")
        query.whereEqualTo("userId", AVUser.getCurrentUser().objectId)
        query.whereEqualTo("isDeleted", false)
        infoLiveQuery = AVLiveQuery.initWithQuery(query)
        query = AVQuery<AVObject>("EmergencyContact")
        query.whereEqualTo("userId", AVUser.getCurrentUser().objectId)
        emergencyLiveQuery = AVLiveQuery.initWithQuery(query)
        query = AVQuery<AVObject>("Call")
        query.whereEqualTo("callerAccountId", AVUser.getCurrentUser().objectId)
        historyLiveQuery = AVLiveQuery.initWithQuery(query)
        infoLiveQuery.setEventHandler(object : AVLiveQueryEventHandler() {
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

            override fun onObjectLeave(avObject: AVObject, updateKeyList: MutableList<String>?) {
                super.onObjectLeave(avObject, updateKeyList)
                MainScope().launch {
                    infoDao.deleteById(avObject.objectId)
                }
            }

            override fun onObjectDeleted(objectId: String) {
                super.onObjectDeleted(objectId)
                MainScope().launch {
                    infoDao.deleteById(objectId)
                }
            }
        })
        emergencyLiveQuery.setEventHandler(object : AVLiveQueryEventHandler() {
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

        })
        historyLiveQuery.setEventHandler(object : AVLiveQueryEventHandler() {
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
        })
        infoLiveQuery.subscribeInBackground(object : AVLiveQuerySubscribeCallback() {
            override fun done(e: AVException?) {
                if (e == null) {
                    emergencyLiveQuery.subscribeInBackground(object :
                        AVLiveQuerySubscribeCallback() {
                        override fun done(e: AVException?) {
                            if (e == null) {
                                historyLiveQuery.subscribeInBackground(object :
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
        infoLiveQuery.unsubscribeInBackground(object : AVLiveQuerySubscribeCallback() {
            override fun done(e: AVException?) {
                emergencyLiveQuery.unsubscribeInBackground(object :
                    AVLiveQuerySubscribeCallback() {
                    override fun done(e: AVException?) {
                        if (e == null) {
                            historyLiveQuery.unsubscribeInBackground(object :
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