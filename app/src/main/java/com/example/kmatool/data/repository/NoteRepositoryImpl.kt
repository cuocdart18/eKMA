package com.example.kmatool.data.repository

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.common.Resource
import com.example.kmatool.data.data_source.database.daos.NoteDao
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.repository.INoteRepository
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : BaseRepositories(), INoteRepository {

    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note.toNoteEntity())
    }

    override suspend fun getNotes(): Resource<List<Note>> {
        return safeDaoCall {
            noteDao.getNotes().map { it.toNote() }
        }
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toNoteEntity())
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toNoteEntity())
    }

    override suspend fun deleteNodes() {
        noteDao.deleteNotes()
    }
}