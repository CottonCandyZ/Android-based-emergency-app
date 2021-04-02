package com.example.emergency

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.example.emergency.model.EmergencyContact
import com.example.emergency.model.Info
import com.example.emergency.model.InfoWithEmergencyContact
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Date
import kotlin.reflect.full.declaredMemberProperties

class WebService {

    suspend fun getInfoWithEmergencyContact(id: String): InfoWithEmergencyContact? =
        withContext(Dispatchers.IO) {
            val queryInfo = AVQuery<AVObject>("Info")
            queryInfo.whereEqualTo("objectId", id)
            val infoResult = queryInfo.find()
            var info: Info? = null
            infoResult.forEach {
                val date = Date(it.getDate("birthdate").time)
                info = Info(
                    id = id,
                    realName = it.getString("realName"),
                    sex = it.getString("sex"),
                    date,
                    phone = it.getString("phone"),
                    weight = it.getInt("weight"),
                    bloodType = it.getString("bloodType"),
                    medicalConditions = it.getString("medicalConditions"),
                    medicalNotes = it.getString("medicalNotes"),
                    allergy = it.getString("allergy"),
                    medications = it.getString("medications"),
                    address = it.getString("address")
                )
            }

            // 查紧急联系人
            val queryEmergencyContact = AVQuery<AVObject>("EmergencyContact")
            queryEmergencyContact.whereEqualTo("infoId", id)
            val emergencyContacts: ArrayList<EmergencyContact> = arrayListOf()
            val emergencyContactsResult = queryEmergencyContact.find()
            emergencyContactsResult.forEach {
                val emergencyContact = EmergencyContact(
                    id = it.objectId,
                    infoId = id,
                    relationship = it.get("relationship") as String,
                    phone = it.get("phone") as String
                )
                emergencyContacts.add(emergencyContact)
            }

            if (info == null) return@withContext null


            return@withContext InfoWithEmergencyContact(
                info!!,
                emergencyContacts
            )
        }


    suspend fun getAbstractInfo(): List<Info> = withContext(Dispatchers.IO) {
        val query = AVQuery<AVObject>("Info")
        query.selectKeys(listOf("userId", "id", "realName", "phone"))
        query.whereEqualTo("userId", AVUser.getCurrentUser().objectId)
        val result = query.find()
        val resultList: ArrayList<Info> = arrayListOf()
        result.forEach {
            val info = Info(
                it.getString("objectId"),
                realName = it.getString("realName"),
                phone = it.getString("phone"),
            )
            resultList.add(info)
        }
        return@withContext resultList
    }

    suspend fun saveInfo(info: Info, saveById: Boolean): String =
        withContext(Dispatchers.IO) {
            val remoteInfo = AVObject("Info")
            remoteInfo.put("userId", AVUser.getCurrentUser().objectId)
            info.javaClass
                .kotlin.declaredMemberProperties
                .forEach {
                    if (it.name != "id") {
                        remoteInfo.put(it.name, it.get(info))
                    }
                }
            if (saveById) {
                remoteInfo.objectId = info.id
            }

            var id = ""
            remoteInfo.saveInBackground().blockingSubscribe(object : Observer<AVObject> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: AVObject) {
                    id = t.objectId
                }

                override fun onError(e: Throwable) {
                    throw e
                }

                override fun onComplete() {
                }
            })
            return@withContext id
        }

    suspend fun saveEmergencyContact(emergencyContact: EmergencyContact, saveById: Boolean) =
        withContext(Dispatchers.IO) {
            val remoteEmergencyContact = AVObject("EmergencyContact")
            emergencyContact.javaClass
                .kotlin.declaredMemberProperties
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

    suspend fun deleteEmergencyContact(id: String) = withContext(Dispatchers.IO) {
        val deleteItem = AVObject.createWithoutData("EmergencyContact", id)
        deleteItem.delete()
    }

    companion object {
        @Volatile
        private var instance: WebService? = null

        fun getInstance(
        ): WebService = instance ?: synchronized(this) {
            instance ?: WebService().also { instance = it }
        }
    }
}