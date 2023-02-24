package com.example.kmatool.fragments.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.kmatool.R
import com.example.kmatool.databinding.FragmentScheduleMainBinding
import com.example.kmatool.databinding.LayoutCalendarDayBinding
import com.example.kmatool.utils.displayText
import com.example.kmatool.utils.makeInVisible
import com.example.kmatool.utils.makeVisible
import com.example.kmatool.utils.setTextColorRes
import com.example.kmatool.view_model.schedule.ScheduleMainViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class ScheduleMainFragment : Fragment() {
    private val TAG = ScheduleMainFragment::class.java.simpleName
    private lateinit var binding: FragmentScheduleMainBinding
    private val navController: NavController by lazy { findNavController() }
    private val scheduleMainViewModel: ScheduleMainViewModel by lazy {
        ViewModelProvider(requireActivity())[ScheduleMainViewModel::class.java]
    }

    // setup calendar
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create $TAG")
        binding = FragmentScheduleMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")
        // setup calendar
        setupCalendar()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy $TAG")
    }

    private fun setupCalendar() {
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
        binding.layoutWeekTitle.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.setTextColorRes(R.color.white)
                val dayOfWeek = daysOfWeek[index]
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title
            }
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view) {
                selectedDate(it)
            }

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date, container.binding, data.position == DayPosition.MonthDate)
            }
        }
        binding.calendarView.monthScrollListener = { updateTitle() }
        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarView.scrollToMonth(currentMonth)
        // Select the first day of the visible month.
        selectedDate(today)
    }

    private fun selectedDate(date: LocalDate) {
        Log.d(TAG, "click date = $date")
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
        }
    }

    private fun bindDate(
        date: LocalDate,
        binding: LayoutCalendarDayBinding,
        isSelectable: Boolean
    ) {
        val textView = binding.tvDay
        val dotView = binding.vNotiDot

        textView.text = date.dayOfMonth.toString()
        if (isSelectable) {
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
                    textView.setTextColorRes(R.color.white)
                    textView.background = null
                    dotView.makeVisible()
                }
            }
        } else {
            textView.setTextColorRes(R.color.gray_1)
            textView.background = null
            dotView.makeInVisible()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val month = binding.calendarView.findFirstVisibleMonth()?.yearMonth ?: return
        Log.d(TAG, "month = $month")
        binding.tvYearTitle.text = month.year.toString()
        binding.tvMonthTitle.text = month.month.displayText(short = false)
    }
}