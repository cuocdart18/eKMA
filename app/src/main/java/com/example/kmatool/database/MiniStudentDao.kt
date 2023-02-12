package com.example.kmatool.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kmatool.models.score.MiniStudent

@Dao
interface MiniStudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(miniStudent: MiniStudent)

    @Query("SELECT * FROM mini_student ORDER BY dateModified DESC LIMIT 20")
    suspend fun getRecentHistorySearch(): List<MiniStudent>
}