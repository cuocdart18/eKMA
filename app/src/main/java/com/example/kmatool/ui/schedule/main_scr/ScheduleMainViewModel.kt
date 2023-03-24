package com.example.kmatool.ui.schedule.main_scr

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.PeriodRepository
import com.example.kmatool.fragments.schedule.syncFormatJsonApi
import com.example.kmatool.data.models.Period
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ScheduleMainViewModel : BaseViewModel() {
    override val TAG = ScheduleMainViewModel::class.java.simpleName

    // global periods
    private lateinit var getListJob: Job
    private lateinit var periodsDayMap: Map<String, List<Period>>

    init {
        logDebug("init $TAG")
    }

    fun getListPeriodFromDatabase(
        context: Context,
        callback: (setEventsDay: List<String>) -> Unit
    ) {
        logDebug("get and filter periods from database")
        // action
        getListJob = viewModelScope.launch(Dispatchers.IO) {
            val periodRepository = PeriodRepository(context)
            val periods = periodRepository.getPeriods()
            // add to Map
            filterListToAddPeriodsDayMap(periods)
            // filter events day
            distinctEventsDay(periods, callback)
            // notify to fragment
            logDebug("get periods successfully")
        }
    }

    private suspend fun filterListToAddPeriodsDayMap(periods: List<Period>) {
        withContext(Dispatchers.Default) {
            periodsDayMap = periods.groupBy { it.day }
        }
    }

    private suspend fun distinctEventsDay(
        periods: List<Period>,
        callback: (eventsDay: List<String>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val eventsDay = mutableListOf<String>()
            periods.distinctBy { it.day }.forEach { eventsDay.add(it.day) }
            logDebug("distinct periods events day = $eventsDay")
            withContext(Dispatchers.Main) {
                callback(eventsDay)
            }
        }
    }

    fun showPeriodsWithDate(
        date: LocalDate,
        callback: (periods: List<Period>?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            if (getListJob.isActive) {
                logDebug("getListJob is active")
                getListJob.join()
            }
            val dateFormatted = date.syncFormatJsonApi()
            logDebug("get periods with date formatted = $dateFormatted")
            // pass to UI
            callback(periodsDayMap[dateFormatted])
        }
    }
}