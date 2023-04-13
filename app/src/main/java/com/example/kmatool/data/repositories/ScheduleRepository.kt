package com.example.kmatool.data.repositories

import android.util.Log
import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.data.local.DataStoreManager
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.services.PeriodLocalService
import com.example.kmatool.data.services.ScheduleRemoteService
import com.example.kmatool.fragments.schedule.convertPeriodsToStartEndTime
import com.example.kmatool.utils.AUTHOR_MESSAGE_ERROR
import com.example.kmatool.utils.jsonObjectToString
import kotlinx.coroutines.*
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val periodLocalService: PeriodLocalService,
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
            // save profile to local
            logDebug("save profile to local")
            saveProfileToLocal(profileResult) {
                Log.d(TAG, "save profile successfully")
            }.join()
            // save login state
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
            // save schedule to database
            logDebug("save schedule to database")
            // format start and end time of period
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
        // convert to json string
        return CoroutineScope(Dispatchers.IO).launch {
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
            it.startTime = timeMap["start"]
            it.endTime = timeMap["end"]
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