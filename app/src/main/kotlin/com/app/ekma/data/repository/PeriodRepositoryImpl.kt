package com.app.ekma.data.repository

import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.common.Resource
import com.app.ekma.data.data_source.apis.ScheduleAPI
import com.app.ekma.data.data_source.database.daos.PeriodDao
import com.app.ekma.data.models.Period
import com.app.ekma.data.models.repository.IPeriodRepository
import javax.inject.Inject

class PeriodRepositoryImpl @Inject constructor(
    private val periodDao: PeriodDao,
    private val scheduleAPI: ScheduleAPI
) : BaseRepositories(), IPeriodRepository {

    override suspend fun insertPeriods(periods: List<Period>) {
        val periodsEntity = periods.map { it.toPeriodEntity() }
        periodDao.insertPeriods(periodsEntity)
    }

    override suspend fun deletePeriods() {
        periodDao.deletePeriods()
    }

    override suspend fun getPeriods(): Resource<List<Period>> {
        return safeDaoCall {
            periodDao.getPeriods().map { it.toPeriod() }
        }
    }

    override suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<List<Period>> {
        return safeApiCall {
            scheduleAPI.getPeriods(username, password, hashed).periods.map { it.toPeriod() }
        }
    }

    override suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean,
        semesterCode: String
    ): Resource<List<Period>> {
        return safeApiCall {
            scheduleAPI.getPeriods(
                username,
                password,
                hashed,
                semesterCode
            ).periods.map { it.toPeriod() }
        }
    }

    override suspend fun getSemesterCodes(): Resource<List<String>> {
        return safeApiCall {
            scheduleAPI.getSemesterCodes()
        }
    }
}