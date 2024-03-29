package com.example.emergency.util

import cn.leancloud.AVObject
import com.example.emergency.data.entity.EmergencyContact
import com.example.emergency.data.entity.History
import com.example.emergency.data.entity.Info

fun convertAVObjectToInfo(avObject: AVObject): Info {
    return Info(
        id = avObject.objectId,
        realName = avObject.getString("realName"),
        sex = avObject.getString("sex"),
        birthdate = avObject.getDate("birthdate"),
        phone = avObject.getString("phone"),
        weight = avObject.getInt("weight"),
        bloodType = avObject.getString("bloodType"),
        medicalConditions = avObject.getString("medicalConditions"),
        medicalNotes = avObject.getString("medicalNotes"),
        allergy = avObject.getString("allergy"),
        medications = avObject.getString("medications"),
        address = avObject.getString("address"),
        chosen = avObject.getBoolean("chosen")
    )
}

fun convertAVObjectToEmergencyContact(avObject: AVObject): EmergencyContact {
    return EmergencyContact(
        id = avObject.objectId,
        infoId = avObject.getString("infoId"),
        relationship = avObject.getString("relationship"),
        phone = avObject.getString("phone"),
    )
}

fun convertAVObjectToHistory(avObject: AVObject): History {
    return History(
        id = avObject.objectId,
        patientName = avObject.getString("patientName"),
        locationName = avObject.getString("locationName"),
        createTime = avObject.createdAt,
        handler = avObject.getString("handler"),
        responseTime = avObject.getDate("responseTime"),
        status = avObject.getString("status"),
        handlerPhone = avObject.getString("handlerPhone")
    )
}