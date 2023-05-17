package com.example.kmatool.data.models.service

import com.example.kmatool.data.models.Period

interface IScheduleService {

    suspend fun deletePeriods()

    suspend fun insertPeriods(periods: List<Period>)

    suspend fun getPeriods(): List<Period>

    suspend fun getPeriods(username: String, password: String, hashed: Boolean): List<Period>
}