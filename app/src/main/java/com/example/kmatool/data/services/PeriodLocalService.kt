package com.example.kmatool.data.services

import com.example.kmatool.data.data_source.database.daos.PeriodDao
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.repository.toPeriod
import com.example.kmatool.data.repository.toPeriodEntity
import javax.inject.Inject

class PeriodLocalService @Inject constructor(
    private val periodDao: PeriodDao
) {
    suspend fun insertPeriods(periods: List<Period>) {
        val periodsEntity = periods.map { it.toPeriodEntity() }
        periodDao.insertPeriods(periodsEntity)
    }

    suspend fun deletePeriods() {
        periodDao.deletePeriods()
    }

    suspend fun getPeriods(): List<Period> {
        return periodDao.getPeriods().map { it.toPeriod() }
    }
}