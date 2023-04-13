package com.example.kmatool.ui.schedule.main_scr

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.PeriodRepository
import com.example.kmatool.fragments.schedule.syncFormatJsonApi
import com.example.kmatool.data.models.Period
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleMainViewModel @Inject constructor(
    private val periodRepository: PeriodRepository
) : BaseViewModel() {
    override val TAG = ScheduleMainViewModel::class.java.simpleName

    // global periods
    private lateinit var getListPeriodJob: Job

    fun getListPeriod(
        callback: (setEventsDay: List<String>) -> Unit
    ) {
        logDebug("get and filter periods from database")
        // action
        getListPeriodJob = viewModelScope.launch(Dispatchers.IO) {
            periodRepository.getListPeriodFromDatabase { setEventsDay ->
                CoroutineScope(Dispatchers.Main).launch {
                    callback(setEventsDay)
                }
            }
            // notify to fragment
            logDebug("get periods successfully")
        }
    }

    fun showPeriodsWithDate(
        date: LocalDate,
        callback: (periods: List<Period>?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            // ensure that getListPeriodJob is done
            if (getListPeriodJob.isActive) {
                logDebug("getListJob is active")
                getListPeriodJob.join()
            }
            val dateFormatted = date.syncFormatJsonApi()
            logDebug("get periods with date formatted = $dateFormatted")
            // pass to UI
            callback(periodRepository.periodsDayMap[dateFormatted])
        }
    }
}