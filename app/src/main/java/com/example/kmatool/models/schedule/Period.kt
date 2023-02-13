package com.example.kmatool.models.schedule

data class Period(
    val className: String,
    val day: String,
    val id: Int,
    val lesson: String,
    val room: String,
    val subjectCode: String,
    val subjectName: String,
    val teacher: String
)