package com.example.kmatool.database

import android.content.Context
import com.example.kmatool.models.schedule.Period

class PeriodRepository(context: Context) : PeriodDao {
    private val db: PeriodDao = AppDatabase.getInstance(context).periodDao()

    override suspend fun insertPeriods(periods: List<Period>) {
        db.insertPeriods(periods)
    }

    override suspend fun deleteAllPeriods(periods: List<Period>) {
        db.deleteAllPeriods(periods)
    }

    override suspend fun getPeriods(): List<Period> {
        return db.getPeriods()
    }
}