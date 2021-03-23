package com.example.emergency.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date


@Entity(tableName = "personal_info")
data class Info(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "real_name") val realName: String,
    @ColumnInfo(name = "sex") val sex: String,
    @ColumnInfo(name = "relationship") val relationship: String,
    @ColumnInfo(name = "birthdate") val birthdate: Date,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "weight") val weight: Int?,
    @ColumnInfo(name = "blood_type") val bloodType: String?,
    @ColumnInfo(name = "medical_conditions") val medicalConditions: String?,
    @ColumnInfo(name = "medical_notes") val medicalNotes: String?,
    @ColumnInfo(name = "allergy") val allergy: String?,
    @ColumnInfo(name = "medications") val medications: String?,
    @ColumnInfo(name = "address") val address: String?,
)

@Entity(tableName = "emergency_contact")
data class EmergencyContact(
    @ColumnInfo(name = "info_id") val infoId: Int, // 这里和唯一的 info id 绑定
    @ColumnInfo(name = "relationship") val relationship: String,
    @ColumnInfo(name = "phone") val phone: String,
)