package com.example.kmatool.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.kmatool.data.models.Period

@Dao
interface PeriodDao {

    @Insert
    suspend fun insertPeriods(periods: List<Period>)

    @Delete
    suspend fun deleteAllPeriods(periods: List<Period>)

    @Query("SELECT * FROM period")
    suspend fun getPeriods(): List<Period>
}