package com.app.ekma.data.models.repository

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Note


interface INoteRepository {

    suspend fun insertNote(note: Note)

    suspend fun getNotes(): Resource<List<Note>>

    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNodes()
}