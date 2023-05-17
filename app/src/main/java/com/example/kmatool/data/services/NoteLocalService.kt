package com.example.kmatool.data.services

import com.example.kmatool.data.data_source.database.daos.NoteDao
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.repository.toNote
import com.example.kmatool.data.repository.toNoteEntity
import javax.inject.Inject

class NoteLocalService @Inject constructor(
    private val noteDao: NoteDao
) {

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note.toNoteEntity())
    }

    suspend fun getNotes(): List<Note> {
        return noteDao.getNotes().map { it.toNote() }
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toNoteEntity())
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toNoteEntity())
    }

    suspend fun deleteNodes() {
        noteDao.deleteNotes()
    }
}