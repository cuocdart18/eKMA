package com.example.kmatool.data.service

import com.example.kmatool.common.Resource
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.repository.INoteRepository
import com.example.kmatool.data.models.service.INoteService
import javax.inject.Inject

class NoteService @Inject constructor(
    private val noteRepository: INoteRepository
) : INoteService {

    override suspend fun insertNote(note: Note) {
        noteRepository.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        noteRepository.deleteNote(note)
    }

    override suspend fun updateNote(note: Note) {
        noteRepository.updateNote(note)
    }

    override suspend fun deleteNotes() {
        noteRepository.deleteNodes()
    }

    override suspend fun getNotes(): Resource<List<Note>> {
        return noteRepository.getNotes()
    }
}