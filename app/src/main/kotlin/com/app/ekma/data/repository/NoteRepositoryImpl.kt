package com.app.ekma.data.repository

import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.common.Resource
import com.app.ekma.data.data_source.database.daos.NoteDao
import com.app.ekma.data.models.Note
import com.app.ekma.data.models.repository.INoteRepository
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