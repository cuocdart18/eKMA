package com.example.kmatool.ui.note.detail

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.data.data_source.app_data.DataLocalManager
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.service.INoteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val dataLocalManager: DataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val noteService: INoteService
) : BaseViewModel() {

    fun onClickDeleteNote(
        note: Note,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            noteService.deleteNote(note)
            withContext(Dispatchers.IO) {
                updateLocalDataRuntime()
                withContext(Dispatchers.Main) {
                    callback()
                }
            }

        }
    }

    private suspend fun updateLocalDataRuntime() {
        coroutineScope {
            val result = noteService.getNotes()
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

    fun refreshDataInRecyclerView() {
        Data.isRefreshClickedEvents.value = true
    }

    fun cancelAlarm(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val isNotify = dataLocalManager.getIsNotifyEventsSPref()
            if (isNotify) {
                alarmEventsScheduler.cancelEvent(note)
            }
        }
    }
}