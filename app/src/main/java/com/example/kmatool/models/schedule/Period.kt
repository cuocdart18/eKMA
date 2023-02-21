package com.example.kmatool.models.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "period")
data class Period(
    val className: String,
    val day: String,
    @PrimaryKey
    val id: Int,
    val lesson: String,
    val room: String?,
    val subjectCode: String,
    val subjectName: String,
    val teacher: String
)