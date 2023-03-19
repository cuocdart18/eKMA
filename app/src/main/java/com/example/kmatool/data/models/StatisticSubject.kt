package com.example.kmatool.data.models

import java.io.Serializable

data class StatisticSubject(
    val failedStudents: Int,
    val passedStudents: Int
) : Serializable
