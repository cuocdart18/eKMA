package com.example.kmatool.fragments.schedule

import android.view.View
import com.example.kmatool.databinding.LayoutCalendarDayBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate

class DayViewContainer(
    view: View,
    callback: (date: LocalDate) -> Unit
) : ViewContainer(view) {
    // Will be set when this container is bound. See the dayBinder.
    lateinit var day: CalendarDay
    val binding = LayoutCalendarDayBinding.bind(view)

    init {
        view.setOnClickListener {
            if (day.position == DayPosition.MonthDate) {
                callback(day.date)
            }
        }
    }
}