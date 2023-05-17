package com.example.kmatool.data.models.repository

import com.example.kmatool.data.models.Note

interface INoteRepository {

    suspend fun insertNote(note: Note)

    suspend fun getNotes(): List<Note>

    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNodes()
}