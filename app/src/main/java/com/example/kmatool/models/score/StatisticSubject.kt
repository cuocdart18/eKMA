package com.example.kmatool.models.score

import java.io.Serializable

data class StatisticSubject(
    val failedStudents: Int,
    val passedStudents: Int
) : Serializable
