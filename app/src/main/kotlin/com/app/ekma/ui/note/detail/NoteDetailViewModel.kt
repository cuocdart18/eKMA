package com.app.ekma.ui.note.detail

import androidx.lifecycle.viewModelScope
import com.app.ekma.alarm.AlarmEventsScheduler
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.Data
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.Note
import com.app.ekma.data.models.service.INoteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val dataLocalManager: IDataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val noteService: INoteService
) : BaseViewModel() {
    override val TAG = NoteDetailViewModel::class.java.simpleName
    lateinit var note: Note

    fun onClickDeleteNote(
        note: Note,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            noteService.deleteNote(note)
            // delete local file
            if (!note.audioPath.isNullOrEmpty()) {
                note.audioPath?.let { File(it).delete() }
            }
            Data.getLocalNotesRuntime(noteService)
            withContext(Dispatchers.Main) {
                callback()
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