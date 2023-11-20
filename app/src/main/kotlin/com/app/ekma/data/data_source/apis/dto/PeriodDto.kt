package com.app.ekma.data.data_source.apis.dto

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
