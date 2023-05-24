package com.example.kmatool.data.models.repository

import com.example.kmatool.common.Resource
import com.example.kmatool.data.models.Period

interface IPeriodRepository {

    suspend fun insertPeriods(periods: List<Period>)

    suspend fun deletePeriods()

    suspend fun getPeriods(): Resource<List<Period>>

    suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<List<Period>>
}