package com.example.kmatool.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.kmatool.data.models.Note

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: Note)

    @Query("SELECT * FROM note")
    suspend fun getNotes(): List<Note>

    @Update
    suspend fun updateNote(note: Note)

    @Query("UPDATE note SET title=:title, content=:content, date=:date, time=:time WHERE id = :id")
    suspend fun updateNote(
        id: Int,
        title: String,
        content: String? = null,
        date: String,
        time: String
    )

    @Delete
    suspend fun deleteNote(note: Note)
}