package com.example.kmatool.ui.note.main_scr

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.ADD_NOTE_MODE
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.DataStoreManager
import com.example.kmatool.common.UPDATE_NOTE_MODE
import com.example.kmatool.common.formatDoubleChar
import com.example.kmatool.common.toDayMonthYear
import com.example.kmatool.common.toHourMinute
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class NoteMainViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {
    override val TAG: String = NoteMainViewModel::class.java.simpleName
    val selectDay = MutableLiveData<String>()
    val selectTime = MutableLiveData<String>()

    // default
    var noteMode = ADD_NOTE_MODE
    var oldNote: Note? = null

    fun getCurrentDayAndTime() {
        logDebug("get current day and time of device")
        viewModelScope.launch(Dispatchers.Main) {
            val day = LocalDate.now().toDayMonthYear()
            selectDay.value = day
            val time = LocalTime.now().toHourMinute()
            selectTime.value = time
        }
    }

    fun updateSelectDay(year: Int, month: Int, dayOfMonth: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            selectDay.value = "${dayOfMonth.formatDoubleChar()}/${month.formatDoubleChar()}/$year"
        }
    }

    fun updateSelectTime(hourOfDay: Int, minute: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            selectTime.value = "${hourOfDay.formatDoubleChar()}:${minute.formatDoubleChar()}"
        }
    }

    fun saveNoteToLocalDatabase(
        note: Note,
        callback: () -> Unit
    ) {
        if (noteMode == UPDATE_NOTE_MODE) {
            oldNote?.let { note.id = it.id }
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (noteMode == ADD_NOTE_MODE) {
                noteRepository.insertNote(note) {
                    CoroutineScope(Dispatchers.Main).launch {
                        callback()
                    }
                }
            } else if (noteMode == UPDATE_NOTE_MODE) {
                noteRepository.updateNote(note) {
                    CoroutineScope(Dispatchers.Main).launch {
                        callback()
                    }
                }
            }
        }
    }

    fun refreshNotesDayMapInDataObject(
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateLocalDataRuntime()
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun setAlarmForNote(context: Context, note: Note) {
        if (noteMode == UPDATE_NOTE_MODE) {
            oldNote?.let { note.id = it.id }
        }
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.isNotifyEventsDataStoreFlow.collect() { state ->
                if (state) {
                    AlarmEventsScheduler(context).scheduleEvent(note)
                    logDebug("set notify event state=$state")
                }
                cancel()
            }
        }
    }

    fun cancelAlarmForOldNote(context: Context, note: Note) {
        oldNote?.let { note.id = it.id }
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.isNotifyEventsDataStoreFlow.collect() { state ->
                if (state) {
                    AlarmEventsScheduler(context).cancelEvent(note)
                    logDebug("cancel notify event state=$state")
                }
                cancel()
            }
        }
    }
}