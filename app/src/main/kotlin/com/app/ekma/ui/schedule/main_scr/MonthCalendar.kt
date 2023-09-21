package com.app.ekma.ui.schedule.main_scr

import android.view.View
import androidx.core.view.isGone
import com.app.ekma.R
import com.app.ekma.common.Data
import com.app.ekma.common.makeInVisible
import com.app.ekma.common.setTextColorRes
import com.app.ekma.common.toDayMonthYear
import com.app.ekma.databinding.LayoutCalendarDayBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class DayViewContainer(
    view: View,
    callback: (day: CalendarDay) -> Unit
) : ViewContainer(view) {
    // Will be set when this container is bound. See the dayBinder.
    lateinit var day: CalendarDay
    val binding = LayoutCalendarDayBinding.bind(view)

    init {
        view.setOnClickListener {
            callback(day)
        }
    }
}

class MonthDayBinderImpl(
    private val notifyDateChanged: (date: LocalDate) -> Unit,
    private val callbackOnClick: (day: CalendarDay) -> Unit
) : MonthDayBinder<DayViewContainer> {

    private val TAG = MonthDayBinderImpl::class.simpleName

    // setup calendar
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    override fun create(view: View): DayViewContainer {
        return DayViewContainer(view) {
            // callback by setOnClick
            selectedDate(it)
        }
    }

    fun selectedDate(day: CalendarDay) {
        val date = day.date
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { notifyDateChanged(it) }
            notifyDateChanged(date)
            callbackOnClick(day)
        } else if (day.position != DayPosition.MonthDate) {
            callbackOnClick(day)
        }
    }

    override fun bind(container: DayViewContainer, data: CalendarDay) {
        container.day = data
        bindDate(data, container.binding)
    }

    private fun bindDate(
        data: CalendarDay,
        binding: LayoutCalendarDayBinding
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val date: LocalDate = data.date
            val textView = binding.tvDay
            val dotViewPeriods = binding.vNotiPeriodDot
            val dotViewNotes = binding.vNotiNoteDot

            textView.text = date.dayOfMonth.toString()
            if (data.position == DayPosition.MonthDate) {
                when (date) {
                    selectedDate -> {
                        textView.setTextColorRes(R.color.white)
                        textView.setBackgroundResource(R.drawable.bgr_day_selected)
                        dotViewPeriods.makeInVisible()
                        dotViewNotes.makeInVisible()
                    }
                    today -> {
                        textView.setTextColorRes(R.color.white)
                        textView.setBackgroundResource(R.drawable.bgr_today)
                        dotViewPeriods.isGone = isDateInPeriodList(date)
                        dotViewNotes.isGone = isDateInNoteList(date)
                    }
                    else -> {
                        textView.setTextColorRes(R.color.gray)
                        textView.background = null
                        dotViewPeriods.isGone = isDateInPeriodList(date)
                        dotViewNotes.isGone = isDateInNoteList(date)
                    }
                }
            } else {
                textView.setTextColorRes(R.color.gray_2)
                textView.background = null
                dotViewPeriods.isGone = isDateInPeriodList(date)
                dotViewNotes.isGone = isDateInNoteList(date)
            }
        }
    }

    private fun isDateInPeriodList(date: LocalDate): Boolean {
        return Data.periodsDayMap[date.toDayMonthYear()] == null
    }

    private fun isDateInNoteList(date: LocalDate): Boolean {
        return Data.notesDayMap[date.toDayMonthYear()] == null
    }
}