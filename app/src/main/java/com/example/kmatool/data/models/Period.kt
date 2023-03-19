package com.example.kmatool.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "period")
data class Period(
    val className: String,
    val day: String,
    @PrimaryKey
    val id: Int,
    val lesson: String,
    var startTime: String?,
    var endTime: String?,
    val room: String?,
    val subjectCode: String,
    val subjectName: String,
    val teacher: String
)