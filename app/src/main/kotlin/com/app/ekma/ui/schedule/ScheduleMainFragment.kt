package com.app.ekma.ui.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_NOTE_OBJ
import com.app.ekma.common.displayText
import com.app.ekma.common.makeGone
import com.app.ekma.common.makeVisible
import com.app.ekma.common.pattern.singleton.ClickedDay
import com.app.ekma.common.pattern.singleton.CurrentEventsRefresher
import com.app.ekma.common.pattern.singleton.DownloadScheduleSuccess
import com.app.ekma.common.pattern.singleton.GetScheduleNoteSuccess
import com.app.ekma.common.pattern.singleton.MonthInCalendarRefresher
import com.app.ekma.common.setTextColorRes
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.animation.visible
import com.app.ekma.common.toYearMonth
import com.app.ekma.data.models.Event
import com.app.ekma.data.models.Note
import com.app.ekma.databinding.FragmentScheduleMainBinding
import com.app.ekma.ui.note.detail.NoteDetailFragment
import com.cuocdat.activityutils.getStatusBarHeight
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class ScheduleMainFragment : BaseFragment<FragmentScheduleMainBinding>() {
    override val TAG = ScheduleMainFragment::class.java.simpleName
    private val viewModel by viewModels<ScheduleMainViewModel>()
    private val eventsDayAdapter: EventsDayAdapter by lazy {
        EventsDayAdapter({ onNoteClicked(it) }, { onNoteCheckboxClicked(it) })
    }
    private val dayBinder: MonthDayBinderImpl by lazy {
        MonthDayBinderImpl(
            { binding.calendarView.notifyDateChanged(it) }, { getEventsDay(it) })
    }

    override fun getDataBinding() = FragmentScheduleMainBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewFakeStatus.updateLayoutParams<LinearLayout.LayoutParams> {
            height = getStatusBarHeight
        }
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
        val currentMonth = YearMonth.from(ClickedDay().date)
        val startMonth = currentMonth.minusMonths(60)
        val endMonth = currentMonth.plusMonths(60)
        binding.calendarView.dayBinder = dayBinder
        binding.calendarView.monthScrollListener = { updateTitle() }
        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarView.scrollToMonth(currentMonth)
        dayBinder.selectedDate(ClickedDay())
    }

    @SuppressLint("SetTextI18n")
    private fun getEventsDay(day: CalendarDay) {
        ClickedDay.setData(day)
        // action
        val date = day.date
        // if select day in Month
        if (day.position == DayPosition.MonthDate) {
            binding.googleProgress.makeVisible()
            viewModel.showEventsWithDate(date) { events ->
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
        // pass data
        val bundle = bundleOf(
            KEY_PASS_NOTE_OBJ to note
        )
        // navigate
        parentFragmentManager.commit {
            add<NoteDetailFragment>(R.id.fragment_container_view, args = bundle)
            setReorderingAllowed(true)
            addToBackStack(NoteDetailFragment::class.java.simpleName)
        }
    }

    private fun onNoteCheckboxClicked(note: Note) {
        viewModel.onClickNoteCheckbox(note)
    }

    private fun refreshDataAfterUpdatedOrDeleted() {
        CurrentEventsRefresher().observe(viewLifecycleOwner) { state ->
            if (state) {
                getEventsDay(ClickedDay())
            }
        }
        DownloadScheduleSuccess().observe(viewLifecycleOwner) { state ->
            if (state) {
                binding.calendarView.notifyCalendarChanged()
            }
        }
        GetScheduleNoteSuccess().observe(viewLifecycleOwner) { state ->
            if (state) {
                binding.calendarView.notifyCalendarChanged()
            }
        }
        MonthInCalendarRefresher().observe(viewLifecycleOwner) { month ->
            binding.calendarView.notifyMonthChanged(month)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val month = binding.calendarView.findFirstVisibleMonth()?.yearMonth ?: return
        binding.tvYearTitle.text = month.year.toString()
        binding.tvMonthTitle.gone(true) {
            binding.tvMonthTitle.text = month.month.displayText(short = false)
            binding.tvMonthTitle.visible(true)
        }
    }
}