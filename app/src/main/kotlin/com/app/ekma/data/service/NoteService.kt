package com.app.ekma.data.service

import android.content.Context
import com.app.ekma.common.Resource
import com.app.ekma.data.models.Note
import com.app.ekma.data.models.repository.INoteRepository
import com.app.ekma.data.models.repository.IProfileRepository
import com.app.ekma.data.models.service.INoteService
import javax.inject.Inject

class NoteService @Inject constructor(
    private val profileRepository: IProfileRepository,
    private val noteRepository: INoteRepository
) : INoteService {

    override suspend fun insertNote(note: Note) {
        val myStudentCode = profileRepository.getProfile().studentCode
        noteRepository.insertNote(note, myStudentCode)
    }

    override suspend fun deleteNote(note: Note) {
        val myStudentCode = profileRepository.getProfile().studentCode
        noteRepository.deleteNote(note, myStudentCode)
    }

    override suspend fun deleteAudioNote(context: Context, audioName: String) {
        val myStudentCode = profileRepository.getProfile().studentCode
        noteRepository.deleteAudioNote(context, audioName, myStudentCode)
    }

    override suspend fun updateNote(note: Note) {
        val myStudentCode = profileRepository.getProfile().studentCode
        noteRepository.updateNote(note, myStudentCode)
    }

    override suspend fun deleteNotes() {
        val myStudentCode = profileRepository.getProfile().studentCode
        noteRepository.deleteNotes(myStudentCode)
    }

    override suspend fun getNotes(): Resource<List<Note>> {
        val myStudentCode = profileRepository.getProfile().studentCode
        return noteRepository.getNotes(myStudentCode)
    }
}