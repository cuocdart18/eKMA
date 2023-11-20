package com.app.ekma.data.service

import android.content.Context
import com.app.ekma.common.Resource
import com.app.ekma.data.models.Note
import com.app.ekma.data.models.repository.INoteRepository
import com.app.ekma.data.models.service.INoteService
import javax.inject.Inject

class NoteService @Inject constructor(
    private val noteRepository: INoteRepository
) : INoteService {

    override suspend fun insertNote(note: Note, studentCode: String) {
        noteRepository.insertNote(note, studentCode)
    }

    override suspend fun deleteNote(note: Note, studentCode: String) {
        noteRepository.deleteNote(note, studentCode)
    }

    override suspend fun deleteAudioNote(context: Context, audioName: String, studentCode: String) {
        noteRepository.deleteAudioNote(context, audioName, studentCode)
    }

    override suspend fun updateNote(note: Note, studentCode: String) {
        noteRepository.updateNote(note, studentCode)
    }

    override suspend fun deleteNotes(studentCode: String) {
        noteRepository.deleteNotes(studentCode)
    }

    override suspend fun getNotes(studentCode: String): Resource<List<Note>> {
        return noteRepository.getNotes(studentCode)
    }
}