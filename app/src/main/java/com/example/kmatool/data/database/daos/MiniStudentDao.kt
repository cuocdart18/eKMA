package com.example.kmatool.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kmatool.data.models.MiniStudent

@Dao
interface MiniStudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(miniStudent: MiniStudent)

    @Query("SELECT * FROM mini_student ORDER BY dateModified DESC LIMIT 20")
    suspend fun getRecentHistorySearch(): List<MiniStudent>

    @Query("DELETE FROM mini_student")
    suspend fun deleteRecentHistorySearch()
}