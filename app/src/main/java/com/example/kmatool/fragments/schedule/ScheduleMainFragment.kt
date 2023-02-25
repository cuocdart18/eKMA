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
import com.example.kmatool.utils.setTextColorRes
import com.example.kmatool.view_model.schedule.ScheduleMainViewModel
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import com.kizitonwose.calendar.core.daysOfWeek
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create $TAG")
        binding = FragmentScheduleMainBinding.inflate(inflater, container, false)
        // setup google progress
        setupGoogleProgress()
        // get all of periods from database
        scheduleMainViewModel.getListPeriodFromDatabase(requireContext()) {
            Log.d(TAG, "get periods successfully")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")
        // setup calendar
        setupCalendar()
        // setup rcv
        setupRecyclerViewPeriods()
    }

    private fun setupGoogleProgress() {
        binding.googleProgress.indeterminateDrawable =
            ChromeFloatingCirclesDrawable.Builder(requireContext())
                .colors(resources.getIntArray(R.array.google_colors))
                .build()
        // show load progress
        binding.googleProgress.visibility = View.VISIBLE
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
        binding.calendarView.dayBinder = MonthDayBinderImpl({
            binding.calendarView.notifyDateChanged(it)
        }, {
            onDateClicked(it)
        })
        binding.calendarView.monthScrollListener = { updateTitle() }
        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarView.scrollToMonth(currentMonth)
        // show events of in current day
        onDateClicked(LocalDate.now())
    }

    private fun setupRecyclerViewPeriods() {
        binding.rcvListSubject.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onDateClicked(date: LocalDate) {
        Log.d(TAG, "receive click date = $date listener")
        binding.googleProgress.visibility = View.VISIBLE
        // action
        scheduleMainViewModel.showPeriodsWithDate(date) {
            Log.d(TAG, "date = $date - periods = $it")
            // show load progress
            binding.googleProgress.visibility = View.GONE
            if (it == null) {
                // notify to UI
                binding.tvPeriodsEmpty.visibility = View.VISIBLE
                binding.rcvListSubject.visibility = View.GONE
                binding.tvSumOfSubject.text = "0"
            } else {
                binding.tvPeriodsEmpty.visibility = View.GONE
                binding.rcvListSubject.visibility = View.VISIBLE
                binding.tvSumOfSubject.text = it.size.toString()
                showPeriodsDay(it)
            }
        }
    }

    private fun showPeriodsDay(periods: List<Period>) {
        periodsDayAdapter.setData(periods)
        binding.rcvListSubject.adapter = periodsDayAdapter
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