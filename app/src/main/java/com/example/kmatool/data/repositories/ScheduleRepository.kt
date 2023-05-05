package com.example.kmatool.data.repositories

import android.util.Log
import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.common.Data
import com.example.kmatool.common.DataLocalManager
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.services.PeriodLocalService
import com.example.kmatool.data.services.ScheduleRemoteService
import com.example.kmatool.common.convertPeriodsToStartEndTime
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.services.NoteLocalService
import com.example.kmatool.utils.AUTHOR_MESSAGE_ERROR
import com.example.kmatool.common.jsonObjectToString
import com.example.kmatool.data.models.Event
import kotlinx.coroutines.*
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val periodLocalService: PeriodLocalService,
    private val noteLocalService: NoteLocalService,
    private val scheduleRemoteService: ScheduleRemoteService,
    private val dataLocalManager: DataLocalManager
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
            val job1 = launch {
                val result = periodLocalService.getPeriods()
                withContext(Dispatchers.Main) {
                    Data.periodsDayMap =
                        result.groupBy { it.day } as MutableMap<String, List<Period>>
                    // sort periods on a day by startTime
                    sortPeriodsDayByStartTime()
                }
            }
            val job2 = launch {
                val result = noteLocalService.getNotes()
                withContext(Dispatchers.Main) {
                    Data.notesDayMap =
                        result.groupBy { it.date } as MutableMap<String, List<Note>>
                    // sort notes on a day by day
                    sortNotesDayByTime()
                }
            }
            job1.join()
            job2.join()
            logDebug("get/sort data from local successfully")
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
        val profileResult = scheduleRemoteService.getProfile(username, password, true)
        logInfo("profile message = ${profileResult.message}")

        if (profileResult.message == AUTHOR_MESSAGE_ERROR) {
            return false
        } else {
            logDebug("profile = $profileResult")
            saveProfileToLocal(profileResult) {
                Log.d(TAG, "save profile successfully")
            }.join()
            return true
        }
    }

    suspend fun callScheduleApi(
        username: String,
        password: String,
        setAlarm: (events: List<Event>) -> Unit
    ): Boolean {
        val scheduleResult = scheduleRemoteService.getScheduleData(username, password, true)
        logInfo("schedule message = ${scheduleResult.message}")

        if (scheduleResult.message == AUTHOR_MESSAGE_ERROR) {
            return false
        } else {
            logDebug("schedule = $scheduleResult")
            formatStartEndTime(scheduleResult.periods)
            setAlarm(scheduleResult.periods)
            savePeriodsToDatabase(scheduleResult.periods) {
                logDebug("save schedule successfully")
            }.join()
            return true
        }
    }

    private suspend fun saveProfileToLocal(
        data: Profile,
        callback: () -> Unit
    ): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            // convert to json string
            val dataStringType = async { jsonObjectToString(data) }
            // save
            dataLocalManager.saveProfileSPref(dataStringType.await())
            callback()
        }
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

    private fun formatStartEndTime(periods: List<Period>) {
        periods.forEach {
            val timeMap = convertPeriodsToStartEndTime(it.lesson)
            it.startTime = timeMap["start"]!!
            it.endTime = timeMap["end"]!!
        }
    }

    private suspend fun savePeriodsToDatabase(
        data: List<Period>,
        callback: () -> Unit
    ): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            // action
            periodLocalService.insertPeriods(data)
            // success
            callback()
        }
    }

    suspend fun deletePeriods(
        callback: () -> Unit
    ) {
        periodLocalService.deletePeriods()
        logDebug("delete periods successfully")
        callback()
    }
}