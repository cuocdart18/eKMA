package com.example.kmatool.data.apis.dto

data class PeriodDto(
    val className: String,
    val day: String,
    val id: Int,
    val lesson: String,
    val room: String?,
    val subjectCode: String,
    val subjectName: String,
    val teacher: String
)
