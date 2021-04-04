package com.example.emergency.model

import androidx.room.*
import java.sql.Date

data class AbstractInfo(
    val id: String,
    val realName: String,
    val phone: String,
    var chosen: Boolean,
)


@Entity(tableName = "personal_info")
data class Info(
    @PrimaryKey val id: String = "",
    val realName: String = "",
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
    val chosen: Boolean,
)

@Entity(tableName = "emergency_contact")
data class EmergencyContact(
    @PrimaryKey val id: String = "",
    var infoId: String = "", // 这里和唯一的 info id 绑定
    var relationship: String = "",
    var phone: String = "",
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