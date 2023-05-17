package com.example.kmatool.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.kmatool.data.database.entities.NoteEntity
import com.example.kmatool.data.database.entities.NoteEntityEntry

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: NoteEntity)

    @Query("SELECT * FROM ${NoteEntityEntry.TBL_NAME}")
    suspend fun getNotes(): List<NoteEntity>

    @Query("DELETE FROM ${NoteEntityEntry.TBL_NAME}")
    suspend fun deleteNotes()

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)
}