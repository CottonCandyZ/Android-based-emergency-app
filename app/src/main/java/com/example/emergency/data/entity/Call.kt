package com.example.emergency.data.entity

data class Call(
    val locationCoordinate: String,
    val locationName: String,
    val patientName: String,
    val patientId: String,
    val status: String = "呼救中",
)