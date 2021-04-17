package com.example.emergency.data.remote

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.example.emergency.data.entity.*
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.full.declaredMemberProperties


@Singleton
class WebService @Inject constructor() {
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
                    address = it.getString("address"),
                    chosen = it.getBoolean("chosen")
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
        query.selectKeys(listOf("userId", "id", "realName", "phone", "chosen"))
        query.whereEqualTo("userId", AVUser.getCurrentUser().objectId)
        val result = query.find()
        val resultList: ArrayList<Info> = arrayListOf()
        result.forEach {
            val info = Info(
                it.getString("objectId"),
                realName = it.getString("realName"),
                phone = it.getString("phone"),
                chosen = it.getBoolean("chosen")
            )
            resultList.add(info)
        }
        return@withContext resultList
    }

    suspend fun saveInfo(info: Info, saveById: Boolean): String =
        withContext(Dispatchers.IO) {
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

    suspend fun deleteEmergencyContact(id: String) = withContext(Dispatchers.IO) {
        val deleteItem = AVObject.createWithoutData("EmergencyContact", id)
        deleteItem.delete()
    }

    suspend fun deleteInfo(id: String) = withContext(Dispatchers.IO) {
        val deleteItem = AVObject.createWithoutData("Info", id)
        deleteItem.delete()
    }

    suspend fun updateInfoChosen(removeId: String, updateId: String) = withContext(Dispatchers.IO) {
        val removeItem = AVObject.createWithoutData("Info", removeId)
        val updateItem = AVObject.createWithoutData("Info", updateId)
        removeItem.put("chosen", false)
        updateItem.put("chosen", true)
        AVObject.saveAll(listOf(removeItem, updateItem))

    }

    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        val query = AVQuery<AVObject>("UserSignUp")
        val phone = AVUser.getCurrentUser().mobilePhoneNumber
        query.whereEqualTo("phone", phone)
        val result = query.find()
        var user: User? = null
        result.forEach {
            user = User(
                phone = phone,
                name = it.getString("name")
            )
        }
        return@withContext user
    }



    suspend fun submitOneCall(call: Call) = withContext(Dispatchers.IO) {
        val submit = AVObject("Call")
        submit.put("callerAccountId", AVUser.getCurrentUser().objectId)
        submit.put("callerAccount", AVUser.getCurrentUser().mobilePhoneNumber)
        Call::class.declaredMemberProperties
            .forEach {
                submit.put(it.name, it.get(call))
            }
        submit.save()

    }

}