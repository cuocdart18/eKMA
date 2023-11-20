package com.app.ekma.data.models.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Period

interface IScheduleService {

    suspend fun deletePeriods()

    suspend fun insertPeriods(periods: List<Period>)

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