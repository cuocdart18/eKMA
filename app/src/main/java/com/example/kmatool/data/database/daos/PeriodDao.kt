package com.example.kmatool.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.kmatool.data.database.entities.PeriodEntity
import com.example.kmatool.data.database.entities.PeriodEntityEntry

@Dao
interface PeriodDao {

    @Insert
    suspend fun insertPeriods(periods: List<PeriodEntity>)

    @Delete
    suspend fun deleteAllPeriods(periods: List<PeriodEntity>)

    @Query("SELECT * FROM ${PeriodEntityEntry.TBL_NAME}")
    suspend fun getPeriods(): List<PeriodEntity>

    @Query("DELETE FROM ${PeriodEntityEntry.TBL_NAME}")
    suspend fun deletePeriods()
}