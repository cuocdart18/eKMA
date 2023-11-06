package com.app.ekma.data.models.service

import android.content.Context
import com.app.ekma.common.Resource
import com.app.ekma.data.models.Note

interface INoteService {

    suspend fun insertNote(note: Note, studentCode: String)

    suspend fun deleteNote(note: Note, studentCode: String)

    suspend fun deleteAudioNote(context: Context, audioName: String, studentCode: String)

    suspend fun updateNote(note: Note, studentCode: String)

    suspend fun deleteNotes(studentCode: String)

    suspend fun getNotes(studentCode: String): Resource<List<Note>>
}