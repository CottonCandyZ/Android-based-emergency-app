package com.example.emergency.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "history")
data class History(
    @PrimaryKey val id: Int,

    )