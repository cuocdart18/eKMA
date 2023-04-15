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
        logDebug("insert note=$note to database")
        noteLocalService.insertNote(note)
        logDebug("insert note successfully")
        callback()
    }

    suspend fun updateLocalDataObject() {
        coroutineScope {
            val result = noteLocalService.getNotes()
            withContext(Dispatchers.Main) {
                Data.notesDayMap.value = result.groupBy { it.date }
            }
            logDebug("update data object successfully")
        }
    }
}