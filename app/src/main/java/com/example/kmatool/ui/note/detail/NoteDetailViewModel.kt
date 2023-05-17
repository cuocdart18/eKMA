package com.example.kmatool.ui.note.detail

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.service.INoteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val dataLocalManager: IDataLocalManager,
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
                Data.getLocalNotesRuntime(noteService)
                withContext(Dispatchers.Main) {
                    callback()
                }
            }

        }
    }

    fun refreshDataInRecyclerView() {
        Data.isRefreshClickedEvents.value = true
    }

    fun cancelAlarm(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val isNotify = dataLocalManager.getIsNotifyEvents()
            if (isNotify) {
                alarmEventsScheduler.cancelEvent(note)
            }
        }
    }
}