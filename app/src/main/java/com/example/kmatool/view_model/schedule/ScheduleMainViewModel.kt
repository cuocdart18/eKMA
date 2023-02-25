package com.example.kmatool.view_model.schedule

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmatool.database.PeriodRepository
import com.example.kmatool.fragments.schedule.syncFormatJsonApi
import com.example.kmatool.models.schedule.Period
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ScheduleMainViewModel : ViewModel() {
    private val TAG = ScheduleMainViewModel::class.java.simpleName

    // global periods
    private lateinit var getListJob: Job
    private lateinit var periodsDayMap: Map<String, List<Period>>

    init {
        Log.d(TAG, "init $TAG")
    }

    fun getListPeriodFromDatabase(
        context: Context,
        callback: () -> Unit
    ) {
        Log.d(TAG, "get and filter periods from database")
        // action
        getListJob = viewModelScope.launch(Dispatchers.IO) {
            val periodRepository = PeriodRepository(context)
            val periods = periodRepository.getPeriods()
            // add to Map
            filterListToAddPeriodsDayMap(periods)
            // notify to fragment
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    private suspend fun filterListToAddPeriodsDayMap(periods: List<Period>) {
        withContext(Dispatchers.Default) {
            periodsDayMap = periods.groupBy { it.day }
            /*val daySet = mutableSetOf<String>()
            periods.forEach {
                daySet.add(it.day)
            }
            daySet.forEach { day ->
                val periodsDay = periods.filterList { day == this.day }
                periodsDayMap[day] = periodsDay
            }*/
        }
    }

    fun showPeriodsWithDate(
        date: LocalDate,
        callback: (periods: List<Period>?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            if (getListJob.isActive) {
                getListJob.join()
            }
            val dateFormatted = date.syncFormatJsonApi()
            Log.d(TAG, "get periods with date formatted = $dateFormatted")
            // pass to UI
            callback(periodsDayMap[dateFormatted])
        }
    }
}