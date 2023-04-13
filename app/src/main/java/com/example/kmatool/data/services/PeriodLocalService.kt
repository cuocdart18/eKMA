package com.example.kmatool.data.services

import com.example.kmatool.data.database.daos.PeriodDao
import com.example.kmatool.data.models.Period
import javax.inject.Inject

class PeriodLocalService @Inject constructor(
    private val periodDao: PeriodDao
) {
    suspend fun insertPeriods(periods: List<Period>) {
        periodDao.insertPeriods(periods)
    }

    suspend fun deleteAllPeriods(periods: List<Period>) {
        periodDao.deleteAllPeriods(periods)
    }

    suspend fun getPeriods(): List<Period> {
        return periodDao.getPeriods()
    }
}