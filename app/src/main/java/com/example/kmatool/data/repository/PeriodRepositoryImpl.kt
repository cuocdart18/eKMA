package com.example.kmatool.data.repository

import com.example.kmatool.data.data_source.apis.ScheduleAPI
import com.example.kmatool.data.data_source.database.daos.PeriodDao
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.repository.IPeriodRepository
import javax.inject.Inject

class PeriodRepositoryImpl @Inject constructor(
    private val periodDao: PeriodDao,
    private val scheduleAPI: ScheduleAPI
) : IPeriodRepository {

    override suspend fun insertPeriods(periods: List<Period>) {
        val periodsEntity = periods.map { it.toPeriodEntity() }
        periodDao.insertPeriods(periodsEntity)
    }

    override suspend fun deletePeriods() {
        periodDao.deletePeriods()
    }

    override suspend fun getPeriods(): List<Period> {
        return periodDao.getPeriods().map { it.toPeriod() }
    }

    override suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean
    ): List<Period> {
        val schedule = scheduleAPI.getPeriods(username, password, hashed)
        return schedule.periods.map { it.toPeriod() }
    }
}