package com.app.ekma.data.models.repository

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Period

interface IPeriodRepository {

    suspend fun insertPeriods(periods: List<Period>)

    suspend fun deletePeriods()

    suspend fun getPeriods(): Resource<List<Period>>

    suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<List<Period>>

    suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean,
        semesterCode: String
    ): Resource<List<Period>>

    suspend fun getSemesterCodes(): Resource<List<String>>
}