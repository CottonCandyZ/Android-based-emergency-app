package com.example.emergency.model

import androidx.room.*
import java.sql.Date

data class AbstractInfo(
    val id: Int,
    val realName: String,
    val phone: String,
//    val lastUpdate: Long
)


@Entity(tableName = "personal_info")
data class Info(
    @PrimaryKey val id: Int,
    val realName: String,
    val sex: String = "",
    val birthdate: Date = Date(0),
    val phone: String = "",
    val weight: Int = 0,
    val bloodType: String = "",
    val medicalConditions: String = "",
    val medicalNotes: String = "",
    val allergy: String = "",
    val medications: String = "",
    val address: String = "",
//    val lastUpdate: Long
)

@Entity(tableName = "emergency_contact", primaryKeys = ["infoId", "relationship", "phone"])
data class EmergencyContact(

    val infoId: Int, // 这里和唯一的 info id 绑定
    val relationship: String,
    val phone: String,
)

// 一对多关系
data class InfoWithEmergencyContact(
    @Embedded val info: Info,
    @Relation(
        parentColumn = "id",
        entityColumn = "infoId"
    )
    val emergencyContacts: List<EmergencyContact>
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}