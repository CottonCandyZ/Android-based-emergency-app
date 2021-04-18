package com.example.emergency.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "history")
data class History(
    @PrimaryKey val id: String,
    val patientName: String,
    val locationName: String,
    val createTime: Date,
    val handler: String?,
    val responseTime: Date?,
    val status: String,
)