package com.example.kmatool.ui.schedule.main_scr

import androidx.lifecycle.viewModelScope
import com.example.kmatool.alarm.AlarmEventsScheduler
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.toDayMonthYear
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.service.INoteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleMainViewModel @Inject constructor(
    private val dataLocalManager: IDataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val noteService: INoteService
) : BaseViewModel() {
    override val TAG = ScheduleMainViewModel::class.java.simpleName

    fun showEventsWithDate(
        date: LocalDate,
        callback: (events: List<Event>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val dateFormatted = date.toDayMonthYear()

            val events = mutableListOf<Event>()
            Data.periodsDayMap[dateFormatted]?.let { events.addAll(it) }
            Data.notesDayMap[dateFormatted]?.let { events.addAll(it) }
            val eventsSorted = events.sortedBy { it.getTimeCompare() }

            withContext(Dispatchers.Main) {
                // pass to UI
                callback(eventsSorted)
            }
        }
    }

    fun onClickNoteCheckbox(note: Note) {
        note.isDone = !(note.isDone)
        viewModelScope.launch(Dispatchers.IO) {
            noteService.updateNote(note)
            Data.getLocalNotesRuntime(noteService)
            val isNotify = dataLocalManager.getIsNotifyEvents()
            if (isNotify) {
                if (note.isDone) {
                    alarmEventsScheduler.cancelEvent(note)
                } else {
                    alarmEventsScheduler.scheduleEvent(note)
                }
            }
        }
    }
}