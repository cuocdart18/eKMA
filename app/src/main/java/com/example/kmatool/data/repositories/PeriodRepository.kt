package com.example.kmatool.data.repositories

import android.content.Context
import com.example.kmatool.data.database.AppDatabase
import com.example.kmatool.data.database.daos.PeriodDao
import com.example.kmatool.data.models.Period

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