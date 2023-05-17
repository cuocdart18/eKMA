package com.example.kmatool.ui.schedule.main_scr

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.toDayMonthYear
import com.example.kmatool.data.models.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleMainViewModel @Inject constructor(
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
}