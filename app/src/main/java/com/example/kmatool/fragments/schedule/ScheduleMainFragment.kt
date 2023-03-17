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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kmatool.R
import com.example.kmatool.adapter.schedule.PeriodsDayAdapter
import com.example.kmatool.databinding.FragmentScheduleMainBinding
import com.example.kmatool.models.schedule.Period
import com.example.kmatool.utils.makeGone
import com.example.kmatool.utils.makeVisible
import com.example.kmatool.utils.setTextColorRes
import com.example.kmatool.view_model.schedule.ScheduleMainViewModel
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class ScheduleMainFragment : Fragment() {
    private val TAG = ScheduleMainFragment::class.java.simpleName
    private lateinit var binding: FragmentScheduleMainBinding
    private val scheduleMainViewModel: ScheduleMainViewModel by lazy {
        ViewModelProvider(requireActivity())[ScheduleMainViewModel::class.java]
    }
    private val periodsDayAdapter: PeriodsDayAdapter by lazy { PeriodsDayAdapter() }
    private val dayBinder: MonthDayBinderImpl by lazy {
        MonthDayBinderImpl(
            { binding.calendarView.notifyDateChanged(it) }, { onDateClicked(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create view $TAG")
        binding = FragmentScheduleMainBinding.inflate(inflater, container, false)
        CoroutineScope(Dispatchers.Main).launch {
            // setup google progress
            setupGoogleProgress()
            // setup rcv
            setupRecyclerViewPeriods()
            // setup calendar
            setupCalendar()
        }
        // get all of periods from database
        scheduleMainViewModel.getListPeriodFromDatabase(requireContext()) {
            showDotViewEventsDayInDayBinder(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")
    }

    private fun setupGoogleProgress() {
        binding.googleProgress.indeterminateDrawable =
            ChromeFloatingCirclesDrawable.Builder(requireContext())
                .colors(resources.getIntArray(R.array.google_colors))
                .build()
        // show load progress
        binding.googleProgress.makeVisible()
    }

    private fun setupRecyclerViewPeriods() {
        binding.rcvListSubject.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showPeriodsDay(periods: List<Period>) {
        periodsDayAdapter.setData(periods)
        binding.rcvListSubject.adapter = periodsDayAdapter
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
        binding.calendarView.dayBinder = dayBinder
        binding.calendarView.monthScrollListener = { updateTitle() }
        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarView.scrollToMonth(currentMonth)
        // show events of in current day
        onDateClicked(CalendarDay(LocalDate.now(), DayPosition.MonthDate))
    }

    private fun onDateClicked(day: CalendarDay) {
        val date = day.date
        Log.d(TAG, "receive click date = $date listener")

        // if select day in Month
        if (day.position == DayPosition.MonthDate) {
            binding.googleProgress.makeVisible()
            // action
            scheduleMainViewModel.showPeriodsWithDate(date) {
                Log.d(TAG, "date = $date - periods = $it")
                // show load progress
                binding.googleProgress.makeGone()
                if (it == null) {
                    // notify to UI
                    binding.tvPeriodsEmpty.makeVisible()
                    binding.rcvListSubject.makeGone()
                    binding.tvSumOfSubject.text = "0"
                } else {
                    binding.tvPeriodsEmpty.makeGone()
                    binding.rcvListSubject.makeVisible()
                    binding.tvSumOfSubject.text = it.size.toString()
                    showPeriodsDay(it)
                }
            }
        } else {
            binding.calendarView.scrollToMonth(YearMonth.parse(date.toYearMonth()))
            onDateClicked(CalendarDay(date, DayPosition.MonthDate))
        }
    }

    private fun showDotViewEventsDayInDayBinder(eventsDay: List<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            dayBinder.initEventsDay(eventsDay)
            binding.calendarView.notifyCalendarChanged()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val month = binding.calendarView.findFirstVisibleMonth()?.yearMonth ?: return
        Log.d(TAG, "month = $month")
        binding.tvYearTitle.text = month.year.toString()
        binding.tvMonthTitle.text = month.month.displayText(short = false)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy $TAG")
    }
}