package com.app.ekma.data.data_source.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.ekma.data.data_source.database.entities.MiniStudentEntity
import com.app.ekma.data.data_source.database.entities.MiniStudentEntityEntry

@Dao
interface MiniStudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(miniStudent: MiniStudentEntity)

    @Query("SELECT * FROM ${MiniStudentEntityEntry.TBL_NAME} ORDER BY ${MiniStudentEntityEntry.DATE_MODIFIED}  DESC LIMIT 20")
    suspend fun getRecentHistorySearch(): List<MiniStudentEntity>

    @Query("DELETE FROM ${MiniStudentEntityEntry.TBL_NAME}")
    suspend fun deleteRecentHistorySearch()
}