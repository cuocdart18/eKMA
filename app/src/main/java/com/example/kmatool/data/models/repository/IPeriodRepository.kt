package com.example.kmatool.data.models.repository

import com.example.kmatool.data.models.Period

interface IPeriodRepository {

    suspend fun insertPeriods(periods: List<Period>)

    suspend fun deletePeriods()

    suspend fun getPeriods(): List<Period>

    suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean
    ): List<Period>
}