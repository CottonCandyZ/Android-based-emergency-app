package com.example.emergency.data.remote

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.example.emergency.data.entity.EmergencyContact
import com.example.emergency.data.entity.Info
import com.example.emergency.util.convertAVObjectToEmergencyContact
import com.example.emergency.util.convertAVObjectToInfo
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.full.declaredMemberProperties

@Singleton
class InfoService @Inject constructor() {

    fun getInfo(): List<Info> {
        val query = AVQuery<AVObject>("Info")
        query.whereEqualTo("userId", AVUser.getCurrentUser().objectId)
        query.whereEqualTo("isDeleted", false)
        val infoResult = query.find()
        val resultList: ArrayList<Info> = arrayListOf()
        infoResult.forEach {
            resultList.add(convertAVObjectToInfo(it))
        }
        return resultList
    }

    fun getEmergencyContact(): List<EmergencyContact> {
        val query = AVQuery<AVObject>("EmergencyContact")
        query.whereEqualTo("userId", AVUser.getCurrentUser().objectId)
        val result = query.find()
        val resultList: ArrayList<EmergencyContact> = arrayListOf()
        result.forEach {
            resultList.add(convertAVObjectToEmergencyContact(it))
        }
        return resultList
    }

    fun deleteEmergencyContact(id: String) {
        val deleteItem = AVObject.createWithoutData("EmergencyContact", id)
        deleteItem.delete()
    }

    fun saveInfo(info: Info, saveById: Boolean): String {
        val remoteInfo = AVObject("Info")
        remoteInfo.put("userId", AVUser.getCurrentUser().objectId)
        Info::class.declaredMemberProperties
            .forEach {
                if (it.name != "id") {
                    remoteInfo.put(it.name, it.get(info))
                }
            }
        if (saveById) {
            remoteInfo.objectId = info.id
        }
        var id = ""
        remoteInfo.saveInBackground().blockingSubscribe {
            id = it.objectId
        }
        return id
    }

    fun saveEmergencyContact(emergencyContact: EmergencyContact, saveById: Boolean) {
        val remoteEmergencyContact = AVObject("EmergencyContact")
        remoteEmergencyContact.put("userId", AVUser.getCurrentUser().objectId)
        EmergencyContact::class.declaredMemberProperties
            .forEach {
                if (it.name != "id") {
                    remoteEmergencyContact.put(it.name, it.get(emergencyContact))
                }
            }
        if (saveById) {
            remoteEmergencyContact.objectId = emergencyContact.id
        }
        remoteEmergencyContact.save()
    }

    fun deleteInfo(id: String) {
        val deleteItem = AVObject.createWithoutData("Info", id)
        deleteItem.put("isDeleted", true)
        deleteItem.save()
    }


    fun updateInfoChosen(removeId: String, updateId: String) {
        val removeItem = AVObject.createWithoutData("Info", removeId)
        val updateItem = AVObject.createWithoutData("Info", updateId)
        removeItem.put("chosen", false)
        updateItem.put("chosen", true)
        AVObject.saveAll(listOf(removeItem, updateItem))
    }

    fun updateInfoChosenWithOutRemove(updateId: String) {
        val updateItem = AVObject.createWithoutData("Info", updateId)
        updateItem.put("chosen", true)
        updateItem.save()
    }
}