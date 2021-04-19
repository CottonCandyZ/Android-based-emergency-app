package com.example.emergency.data.entity

data class Call(
    val patientName: String,
    val patientId: String,
    val status: String = "呼救中",
)

data class Location(
    val name: String,
    val coordinate: String
)