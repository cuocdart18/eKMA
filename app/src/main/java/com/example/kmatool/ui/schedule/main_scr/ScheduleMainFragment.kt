package com.example.kmatool.ui.schedule.main_scr

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kmatool.R
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.Data
import com.example.kmatool.common.KEY_PASS_NOTE_OBJ
import com.example.kmatool.databinding.FragmentScheduleMainBinding
import com.example.kmatool.common.displayText
import com.example.kmatool.common.toYearMonth
import com.example.kmatool.data.models.Event
import com.example.kmatool.common.makeGone
import com.example.kmatool.common.makeVisible
import com.example.kmatool.common.setTextColorRes
import com.example.kmatool.data.models.Note
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@AndroidEntryPoint
class ScheduleMainFragment : BaseFragment() {
    override val TAG = ScheduleMainFragment::class.java.simpleName
    private lateinit var binding: FragmentScheduleMainBinding
    private val viewModel by viewModels<ScheduleMainViewModel>()
    private val eventsDayAdapter: EventsDayAdapter by lazy { EventsDayAdapter { onNoteClicked(it) } }
    private val dayBinder: MonthDayBinderImpl by lazy {
        MonthDayBinderImpl(
            { binding.calendarView.notifyDateChanged(it) }, { getEventsDay(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGoogleProgress(binding.googleProgress)
        setupRecyclerViewEvents()
        setupCalendar()
        refreshDataAfterUpdatedOrDeleted()
    }

    private fun setupRecyclerViewEvents() {
        binding.rcvListEvent.layoutManager = LinearLayoutManager(requireContext())
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
        val currentMonth = YearMonth.from(Data.saveDateClicked.date)
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        binding.calendarView.dayBinder = dayBinder
        binding.calendarView.monthScrollListener = { updateTitle() }
        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarView.scrollToMonth(currentMonth)
        dayBinder.selectedDate(Data.saveDateClicked)
    }

    private fun getEventsDay(day: CalendarDay) {
        Data.saveDateClicked = day
        // action
        val date = day.date
        logDebug("onDateClicked = $date")
        // if select day in Month
        if (day.position == DayPosition.MonthDate) {
            binding.googleProgress.makeVisible()
            viewModel.showEventsWithDate(date) { events ->
                logInfo("day = $date - periods/notes = $events")
                binding.googleProgress.makeGone()
                if (events.isEmpty()) {
                    binding.tvEventsEmpty.makeVisible()
                    binding.rcvListEvent.makeGone()
                    binding.tvSumOfEvent.text = "0"
                } else {
                    showEventsDay(events)
                    binding.tvEventsEmpty.makeGone()
                    binding.rcvListEvent.makeVisible()
                    binding.tvSumOfEvent.text = events.size.toString()
                }
            }
        } else {
            binding.calendarView.scrollToMonth(YearMonth.parse(date.toYearMonth()))
            getEventsDay(CalendarDay(date, DayPosition.MonthDate))
        }
    }

    private fun showEventsDay(events: List<Event>) {
        eventsDayAdapter.setData(events)
        binding.rcvListEvent.adapter = eventsDayAdapter
    }

    private fun onNoteClicked(note: Note) {
        logDebug("on click note=${note.title}")
        // pass data
        val bundle = bundleOf(
            KEY_PASS_NOTE_OBJ to note
        )
        // navigate
        navigateToFragment(R.id.noteDetailFragment, bundle)
    }

    private fun refreshDataAfterUpdatedOrDeleted() {
        Data.isRefreshClickedEvents.observe(viewLifecycleOwner) { state ->
            logDebug("refresh events state=$state")
            if (state) {
                getEventsDay(Data.saveDateClicked)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val month = binding.calendarView.findFirstVisibleMonth()?.yearMonth ?: return
        logDebug("updateTitle month = $month")
        binding.tvYearTitle.text = month.year.toString()
        binding.tvMonthTitle.text = month.month.displayText(short = false)
    }
}