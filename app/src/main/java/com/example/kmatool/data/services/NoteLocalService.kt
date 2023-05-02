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

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun updateNoteV2(note: Note) {
        noteDao.updateNote(note.id, note.title, note.content, note.date, note.time)
    }
}