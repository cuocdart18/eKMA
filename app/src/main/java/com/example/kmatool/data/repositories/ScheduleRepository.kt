package com.example.kmatool.data.repositories

import android.util.Log
import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.common.Data
import com.example.kmatool.data.local.DataStoreManager
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.services.PeriodLocalService
import com.example.kmatool.data.services.ScheduleRemoteService
import com.example.kmatool.common.convertPeriodsToStartEndTime
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.services.NoteLocalService
import com.example.kmatool.utils.AUTHOR_MESSAGE_ERROR
import com.example.kmatool.utils.jsonObjectToString
import kotlinx.coroutines.*
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val periodLocalService: PeriodLocalService,
    private val noteLocalService: NoteLocalService,
    private val scheduleRemoteService: ScheduleRemoteService,
    private val dataStoreManager: DataStoreManager,
) : BaseRepositories() {
    override val TAG: String = ScheduleRepository::class.java.simpleName

    suspend fun getLoginState(callback: (res: Boolean) -> Unit) {
        coroutineScope {
            dataStoreManager.isLoginDataStoreFlow.collect { state ->
                logDebug("login state = $state")
                callback(state)
                cancel()
            }
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
                    sortPeriodsValueByStartTime()
                }
            }
            val job2 = launch {
                val result = noteLocalService.getNotes()
                withContext(Dispatchers.Main) {
                    Data.notesDayMap =
                        result.groupBy { it.date } as MutableMap<String, List<Note>>
                    // sort notes on a day by startTime
                    sortNotesValueByTime()
                }
            }
            job1.join()
            job2.join()
            logDebug("get/sort data from local successfully")
        }
    }

    private fun sortPeriodsValueByStartTime() {
        Data.periodsDayMap.forEach { (t, u) ->
            val newPeriods = u.sortedBy { it.startTime }
            Data.periodsDayMap[t] = newPeriods
        }
    }

    private fun sortNotesValueByTime() {
        Data.notesDayMap.forEach { (t, u) ->
            val newNotes = u.sortedBy { it.time }
            Data.notesDayMap[t] = newNotes
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

            logDebug("save profile to local")
            saveProfileToLocal(profileResult) {
                Log.d(TAG, "save profile successfully")
            }.join()

            logDebug("save login state to local")
            saveLoginStateToLocal(true) {
                logDebug("save login state successfully")
            }.join()
            return true
        }
    }

    suspend fun callScheduleApi(
        username: String,
        password: String
    ): Boolean {
        val scheduleResult = scheduleRemoteService.getScheduleData(username, password, true)
        logInfo("schedule message = ${scheduleResult.message}")

        if (scheduleResult.message == AUTHOR_MESSAGE_ERROR) {
            return false
        } else {
            logDebug("schedule = $scheduleResult")
            logDebug("save schedule to database")

            formatStartEndTime(scheduleResult.periods)
            savePeriodsToDatabase(scheduleResult.periods) {
                logDebug("save schedule successfully")
            }
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
            dataStoreManager.storeProfile(dataStringType.await())
            callback()
        }
    }

    private suspend fun saveLoginStateToLocal(
        data: Boolean,
        callback: () -> Unit
    ): Job {
        // save
        return CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.storeIsLogin(data)
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
    ) {
        // action
        periodLocalService.insertPeriods(data)
        // success
        callback()
    }
}