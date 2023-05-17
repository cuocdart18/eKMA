package com.example.kmatool.data.repositories

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.common.Data
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.services.NoteLocalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteLocalService: NoteLocalService
) : BaseRepositories() {
    override val TAG: String = NoteRepository::class.java.simpleName

    suspend fun insertNote(
        note: Note,
        callback: () -> Unit
    ) {
        noteLocalService.insertNote(note)
        callback()
    }

    suspend fun deleteNote(
        note: Note,
        callback: () -> Unit
    ) {
        noteLocalService.deleteNote(note)
        callback()
    }

    suspend fun updateNote(
        note: Note,
        callback: () -> Unit
    ) {
        noteLocalService.updateNote(note)
        callback()
    }

    suspend fun deleteNotes() {
        noteLocalService.deleteNodes()
    }

    suspend fun updateLocalDataRuntime() {
        coroutineScope {
            val result = noteLocalService.getNotes()
            withContext(Dispatchers.Main) {
                Data.notesDayMap =
                    result.groupBy { it.date } as MutableMap<String, List<Note>>
                // sort notes on a day by startTime
                sortNotesValueByTime()
            }
        }
    }

    private fun sortNotesValueByTime() {
        Data.notesDayMap.forEach { (t, u) ->
            val newNotes = u.sortedBy { it.time }
            Data.notesDayMap[t] = newNotes
        }
    }
}