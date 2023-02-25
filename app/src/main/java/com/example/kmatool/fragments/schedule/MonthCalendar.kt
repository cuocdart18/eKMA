package com.example.kmatool.fragments.schedule

import android.util.Log
import android.view.View
import com.example.kmatool.R
import com.example.kmatool.databinding.LayoutCalendarDayBinding
import com.example.kmatool.utils.makeInVisible
import com.example.kmatool.utils.makeVisible
import com.example.kmatool.utils.setTextColorRes
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
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

class MonthDayBinderImpl(
    private val notifyDateChanged: (date: LocalDate) -> Unit,
    private val callbackOnClick: (date: LocalDate) -> Unit
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

    override fun bind(container: DayViewContainer, data: CalendarDay) {
        container.day = data
        bindDate(data, container.binding)
    }

    private fun selectedDate(date: LocalDate) {
        if (selectedDate != date) {
            Log.d(TAG, "clicked date = $date")
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { notifyDateChanged(it) }
            notifyDateChanged(date)
            callbackOnClick(date)
        }
    }

    private fun bindDate(
        data: CalendarDay,
        binding: LayoutCalendarDayBinding
    ) {
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
                    dotView.makeVisible()
                }
                else -> {
                    textView.setTextColorRes(R.color.black)
                    textView.background = null
                    dotView.makeVisible()
                }
            }
        } else {
            textView.setTextColorRes(R.color.gray)
            textView.background = null
            dotView.makeInVisible()
        }
    }
}