package com.example.kmatool.ui.schedule.main_scr

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.toDayMonthYear
import com.example.kmatool.data.models.Period
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleMainViewModel @Inject constructor(
) : BaseViewModel() {
    override val TAG = ScheduleMainViewModel::class.java.simpleName

    fun showEventsWithDate(
        date: LocalDate,
        callback: (periods: List<Period>?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val dateFormatted = date.toDayMonthYear()
            logDebug("get events with date formatted = $dateFormatted")
            // pass to UI
            callback(Data.periodsDayMap.value?.get(dateFormatted))
        }
    }
}