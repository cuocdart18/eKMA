package com.example.kmatool.ui.schedule.login

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.ScheduleRepository
import com.example.kmatool.data.repositories.PeriodRepository
import com.example.kmatool.fragments.schedule.convertPeriodsToStartEndTime
import com.example.kmatool.data.repositories.DataStoreManager
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.Profile
import com.example.kmatool.utils.AUTHOR_MESSAGE_ERROR
import com.example.kmatool.utils.jsonObjectToString
import kotlinx.coroutines.*

class ScheduleLoginViewModel : BaseViewModel() {
    override val TAG = ScheduleLoginViewModel::class.java.simpleName
    private val scheduleRepository: ScheduleRepository by lazy { ScheduleRepository() }

    // observable field
    var isValid = ObservableField<Boolean>()
    var isShowProgress = ObservableField<Boolean>()

    init {
        // hide text view invalid author
        isValid.set(true)
        // hide progress
        isShowProgress.set(false)
    }

    fun handleOnClickBtnLogin(
        context: Context,
        username: String,
        password: String,
        callback: () -> Unit
    ) {
        logInfo("handle input username = $username, password (md5 hashed) = $password")
        // show progress
        isShowProgress.set(true)
        // hide text view invalid author
        isValid.set(true)
        // action
        if (username.isNotBlank() && password.isNotBlank()) {
            logDebug("valid input")
            viewModelScope.launch(Dispatchers.IO) {
                // call profile
                val profileCallState = async { callProfileApi(context, username, password) }
                // call schedule
                val scheduleCallState = async { callScheduleApi(context, username, password) }
                // callback
                if (profileCallState.await() && scheduleCallState.await()) {
                    withContext(Dispatchers.Main) {
                        // hide text view invalid author
                        isValid.set(true)
                        isShowProgress.set(false)
                        callback()
                    }
                } else {
                    logDebug("login denied")
                    isValid.set(false)
                    // hide progress
                    isShowProgress.set(false)
                }
            }
        } else {
            logDebug("invalid input")
            // if invalid input
            isValid.set(false)
            // hide progress
            isShowProgress.set(false)
        }
    }

    private suspend fun callProfileApi(
        context: Context,
        username: String,
        password: String
    ): Boolean {
        val profileResult = scheduleRepository.getProfile(username, password, true)
        logInfo("profile message = ${profileResult.message}")

        if (profileResult.message == AUTHOR_MESSAGE_ERROR) {
            return false
        } else {
            logDebug("profile = $profileResult")
            // save profile to local
            logDebug("save profile to local")
            saveProfileToLocal(context, profileResult) {
                Log.d(TAG, "save profile successfully")
            }.join()
            // save login state
            Log.d(TAG, "save login state to local")
            saveLoginStateToLocal(context, true) {
                logDebug("save login state successfully")
            }.join()
            return true
        }
    }

    private suspend fun callScheduleApi(
        context: Context,
        username: String,
        password: String
    ): Boolean {
        val scheduleResult = scheduleRepository.getScheduleData(username, password, true)
        logInfo("schedule message = ${scheduleResult.message}")

        if (scheduleResult.message == AUTHOR_MESSAGE_ERROR) {
            return false
        } else {
            logDebug("schedule = $scheduleResult")
            // save schedule to database
            logDebug("save schedule to database")
            // format start and end time of period
            formatStartEndTime(scheduleResult.periods)
            savePeriodsToDatabase(context, scheduleResult.periods) {
                logDebug("save schedule successfully")
            }
            return true
        }
    }

    private fun formatStartEndTime(periods: List<Period>) {
        periods.forEach {
            val timeMap = convertPeriodsToStartEndTime(it.lesson)
            it.startTime = timeMap["start"]
            it.endTime = timeMap["end"]
        }
    }

    private suspend fun saveProfileToLocal(
        context: Context,
        data: Profile,
        callback: () -> Unit
    ): Job {
        // convert to json string
        return viewModelScope.launch(Dispatchers.IO) {
            val dataStringType = async { jsonObjectToString(data) }
            // save
            val dataStoreManager = DataStoreManager(context)
            dataStoreManager.storeProfile(dataStringType.await())
            callback()
        }
    }

    private suspend fun saveLoginStateToLocal(
        context: Context,
        data: Boolean,
        callback: () -> Unit
    ): Job {
        // save
        return viewModelScope.launch(Dispatchers.IO) {
            val dataStoreManager = DataStoreManager(context)
            dataStoreManager.storeIsLogin(data)
            callback()
        }
    }

    private suspend fun savePeriodsToDatabase(
        context: Context,
        data: List<Period>,
        callback: () -> Unit
    ) {
        val periodRepository = PeriodRepository(context)
        // action
        periodRepository.insertPeriods(data)
        // success
        callback()
    }
}