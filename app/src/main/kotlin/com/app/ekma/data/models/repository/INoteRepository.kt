package com.app.ekma.data.models.repository

import android.content.Context
import com.app.ekma.common.Resource
import com.app.ekma.data.models.Note


interface INoteRepository {

    suspend fun insertNote(note: Note, myStudentCode: String)

    suspend fun getNotes(myStudentCode: String): Resource<List<Note>>

    suspend fun deleteNote(note: Note, myStudentCode: String)

    suspend fun deleteAudioNote(context: Context, audioName: String, studentCode: String)

    suspend fun updateNote(note: Note, myStudentCode: String)

    suspend fun deleteNotes(myStudentCode: String)
}