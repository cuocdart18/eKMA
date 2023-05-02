package com.example.kmatool.common

import androidx.lifecycle.MutableLiveData
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.Period
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.LocalDate

object Data {
    // K = day
    // V = Periods/Notes on a day
    var periodsDayMap = mutableMapOf<String, List<Period>>()
    var notesDayMap = mutableMapOf<String, List<Note>>()

    val isRefreshClickedEvents = MutableLiveData<Boolean>()
    var saveDateClicked = CalendarDay(LocalDate.now(), DayPosition.MonthDate)
}