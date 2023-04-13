package com.example.kmatool.data.repositories

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.services.PeriodLocalService
import kotlinx.coroutines.*
import javax.inject.Inject

class PeriodRepository @Inject constructor(
    private val periodLocalService: PeriodLocalService
) : BaseRepositories() {
    override val TAG: String = PeriodRepository::class.java.simpleName

    // global periods
    lateinit var periodsDayMap: Map<String, List<Period>>

    suspend fun getListPeriodFromDatabase(
        callback: (setEventsDay: List<String>) -> Unit
    ) {
        logDebug("get and filter periods from database")
        // action
        coroutineScope {
            val periods = periodLocalService.getPeriods()
            // add to Map
            filterListToAddPeriodsDayMap(periods)
            // filter events day
            distinctEventsDay(periods) { setEventsDay ->
                callback(setEventsDay)
            }
        }
    }

    private suspend fun filterListToAddPeriodsDayMap(
        periods: List<Period>
    ) {
        withContext(Dispatchers.Default) {
            periodsDayMap = periods.groupBy { it.day }
        }
    }

    private suspend fun distinctEventsDay(
        periods: List<Period>,
        callback: (eventsDay: List<String>) -> Unit
    ) {
        withContext(Dispatchers.Default) {
            val setEventsDay = mutableListOf<String>()
            periods.distinctBy { it.day }.forEach { setEventsDay.add(it.day) }
            logDebug("distinct periods events day = $setEventsDay")
            callback(setEventsDay)
        }
    }
}