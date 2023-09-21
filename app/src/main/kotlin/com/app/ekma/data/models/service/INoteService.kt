package com.app.ekma.data.models.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Note

interface INoteService {

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNotes()

    suspend fun getNotes(): Resource<List<Note>>
}