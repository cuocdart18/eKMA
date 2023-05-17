package com.example.kmatool.data.models.service

import com.example.kmatool.data.models.Note

interface INoteService {

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNotes()

    suspend fun getNotes(): List<Note>
}