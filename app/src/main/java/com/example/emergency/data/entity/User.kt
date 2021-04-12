package com.example.emergency.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    val name: String,
    @PrimaryKey
    val phone: String,
)