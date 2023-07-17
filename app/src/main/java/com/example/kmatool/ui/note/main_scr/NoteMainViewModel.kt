package com.example.kmatool.ui.note.main_scr

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.ADD_NOTE_MODE
import com.example.kmatool.alarm.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.common.UPDATE_NOTE_MODE
import com.example.kmatool.common.formatDoubleChar
import com.example.kmatool.common.toDayMonthYear
import com.example.kmatool.common.toHourMinute
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.service.INoteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class NoteMainViewModel @Inject constructor(
    private val dataLocalManager: IDataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val noteService: INoteService
) : BaseViewModel() {
    override val TAG: String = NoteMainViewModel::class.java.simpleName
    val selectDay = MutableLiveData<String>()
    val selectTime = MutableLiveData<String>()

    // default mode
    var noteMode = ADD_NOTE_MODE

    // update note mode
    lateinit var oldNote: Note

    fun getCurrentDayAndTime() {
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
        // define primary key for note
        note.id = note.hashCode()
        viewModelScope.launch(Dispatchers.IO) {
            if (noteMode == ADD_NOTE_MODE) {
                noteService.insertNote(note)
            } else if (noteMode == UPDATE_NOTE_MODE) {
                note.isDone = oldNote.isDone
                noteService.deleteNote(oldNote)
                noteService.insertNote(note)
                cancelAlarmForOldNote()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun refreshNotesDayMapInDataObject(
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Data.getLocalNotesRuntime(noteService)
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun setAlarmForNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val isNotify = dataLocalManager.getIsNotifyEvents()
            if (isNotify && !note.isDone) {
                alarmEventsScheduler.scheduleEvent(note)
            }
        }
    }

    private fun cancelAlarmForOldNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val isNotify = dataLocalManager.getIsNotifyEvents()
            if (isNotify) {
                alarmEventsScheduler.cancelEvent(oldNote)
            }
        }
    }
}