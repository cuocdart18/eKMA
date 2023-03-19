package com.example.kmatool.fragments.schedule

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.example.kmatool.R
import com.example.kmatool.databinding.LayoutCalendarDayBinding
import com.example.kmatool.utils.makeInVisible
import com.example.kmatool.utils.setTextColorRes
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
    private var eventsDay: List<String>? = null

    // setup calendar
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    init {
        Log.d(TAG, "init $TAG")
    }

    override fun create(view: View): DayViewContainer {
        return DayViewContainer(view) {
            // callback by setOnClick
            selectedDate(it)
        }
    }

    override fun bind(container: DayViewContainer, data: CalendarDay) {
        container.day = data
        bindDate(data, container.binding)
    }

    private fun selectedDate(day: CalendarDay) {
        val date = day.date
        if (selectedDate != date) {
            Log.d(TAG, "clicked date = $date")
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { notifyDateChanged(it) }
            notifyDateChanged(date)
            callbackOnClick(day)
        } else if (day.position != DayPosition.MonthDate) {
            callbackOnClick(day)
        }
    }

    private fun bindDate(
        data: CalendarDay,
        binding: LayoutCalendarDayBinding
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val date: LocalDate = data.date
            val textView = binding.tvDay
            val dotView = binding.vNotiDot

            textView.text = date.dayOfMonth.toString()
            if (data.position == DayPosition.MonthDate) {
                when (date) {
                    selectedDate -> {
                        Log.d(TAG, "set background date clicked = $date")
                        textView.setTextColorRes(R.color.white)
                        textView.setBackgroundResource(R.drawable.bgr_day_selected)
                        dotView.makeInVisible()
                    }
                    today -> {
                        Log.d(TAG, "set background current date = $date")
                        textView.setTextColorRes(R.color.white)
                        textView.setBackgroundResource(R.drawable.bgr_today)
                        dotView.isVisible = isDateInEventList(date)
                    }
                    else -> {
                        textView.setTextColorRes(R.color.black)
                        textView.background = null
                        dotView.isVisible = isDateInEventList(date)
                    }
                }
            } else {
                textView.setTextColorRes(R.color.gray)
                textView.background = null
                dotView.isVisible = isDateInEventList(date)
            }
        }
    }

    fun initEventsDay(eventsDay: List<String>) {
        Log.d(TAG, "init events day")
        this.eventsDay = eventsDay
    }

    private fun isDateInEventList(date: LocalDate): Boolean {
        if (eventsDay == null) {
            return false
        } else {
            val dateFormatted = date.syncFormatJsonApi()
            return eventsDay!!.contains(dateFormatted)
        }
    }
}