package com.example.kmatool.data.services

import com.example.kmatool.data.database.daos.NoteDao
import com.example.kmatool.data.models.Note
import javax.inject.Inject

class NoteLocalService @Inject constructor(
    private val noteDao: NoteDao
) {

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun getNotes(): List<Note> {
        return noteDao.getNotes()
    }
}