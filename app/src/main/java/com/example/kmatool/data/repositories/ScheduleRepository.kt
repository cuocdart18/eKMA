package com.example.kmatool.data.repositories

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.data.app_data.DataLocalManager
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.services.PeriodLocalService
import com.example.kmatool.data.services.ScheduleRemoteService
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.services.NoteLocalService
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.toProfileShPref
import kotlinx.coroutines.*
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val periodLocalService: PeriodLocalService,
    private val noteLocalService: NoteLocalService,
    private val scheduleRemoteService: ScheduleRemoteService,
    private val dataLocalManager: DataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler
) : BaseRepositories() {
    override val TAG: String = ScheduleRepository::class.java.simpleName

    suspend fun getLoginState(callback: (res: Boolean) -> Unit) {
        coroutineScope {
            val state = dataLocalManager.getLoginStateSPref()
            callback(state)
        }
    }

    suspend fun getLocalData() {
        coroutineScope {
            val job1 = launch { getLocalPeriodsRuntime() }
            val job2 = launch { getLocalNotesRuntime() }
            job1.join()
            job2.join()
        }
    }

    suspend fun getLocalPeriodsRuntime() {
        val result = periodLocalService.getPeriods()
        withContext(Dispatchers.Default) {
            Data.periodsDayMap =
                result.groupBy { it.day } as MutableMap<String, List<Period>>
            // sort periods on a day by startTime
            sortPeriodsDayByStartTime()
        }
    }

    private suspend fun getLocalNotesRuntime() {
        val result = noteLocalService.getNotes()
        withContext(Dispatchers.Default) {
            Data.notesDayMap =
                result.groupBy { it.date } as MutableMap<String, List<Note>>
            // sort notes on a day by day
            sortNotesDayByTime()
        }
    }

    private fun sortPeriodsDayByStartTime() {
        Data.periodsDayMap.forEach { (t, u) ->
            Data.periodsDayMap[t] = u.sortedBy { it.startTime }
        }
    }

    private fun sortNotesDayByTime() {
        Data.notesDayMap.forEach { (t, u) ->
            Data.notesDayMap[t] = u.sortedBy { it.time }
        }
    }

    suspend fun callProfileApi(
        username: String,
        password: String
    ): Boolean {
        return coroutineScope {
            val profileResult = scheduleRemoteService.getProfile(username, password, true)
            logDebug("profile = $profileResult")
            saveProfileToLocal(profileResult)
            return@coroutineScope true
        }
    }

    suspend fun callScheduleApi(
        username: String,
        password: String
    ): Boolean {
        return coroutineScope {
            val periods = scheduleRemoteService.getPeriods(username, password, true)
            setAlarmPeriodsInFirstTime(periods)
            deletePeriods() // delete old periods if conflict
            savePeriodsToDatabase(periods)
            return@coroutineScope true
        }
    }

    private suspend fun setAlarmPeriodsInFirstTime(events: List<Event>) {
        if (dataLocalManager.getIsNotifyEventsSPref()) {
            alarmEventsScheduler.scheduleEvents(events)
        }
    }

    private suspend fun saveProfileToLocal(data: Profile) {
        coroutineScope {
            dataLocalManager.saveProfileSPref(data.toProfileShPref())
        }
    }

    private suspend fun savePeriodsToDatabase(data: List<Period>) {
        periodLocalService.insertPeriods(data)
    }

    suspend fun deletePeriods() {
        periodLocalService.deletePeriods()
    }

    suspend fun saveLoginStateToLocal(
        data: Boolean,
        callback: () -> Unit
    ): Job {
        // save
        return CoroutineScope(Dispatchers.IO).launch {
            dataLocalManager.saveLoginStateSPref(data)
            callback()
        }
    }
}