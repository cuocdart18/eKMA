package com.example.kmatool.activities

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.repositories.DataStoreManager
import com.example.kmatool.data.repositories.PeriodRepository
import com.example.kmatool.data.repositories.ScheduleRepository
import com.example.kmatool.fragments.schedule.convertPeriodsToStartEndTime
import com.example.kmatool.utils.AUTHOR_MESSAGE_ERROR
import com.example.kmatool.utils.jsonObjectToString
import kotlinx.coroutines.*

class LoginViewModel : ViewModel() {
    private val TAG = LoginViewModel::class.java.simpleName
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

    fun getLoginState(
        context: Context,
        callback: (res: Boolean) -> Unit
    ) {
        Log.d(TAG, "get login state")
        isShowProgress.set(true)
        viewModelScope.launch(Dispatchers.IO) {
            val dataStoreManager = DataStoreManager(context)
            dataStoreManager.isLoginDataStoreFlow.collect { state ->
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "login state = $state")
                    if (state) {
                        delay(1500)
                        isShowProgress.set(false)
                        callback(state)
                    } else {
                        isShowProgress.set(false)
                    }
                }
                cancel()
            }
        }
    }

    fun handleOnClickBtnLogin(
        context: Context,
        username: String,
        password: String,
        callback: () -> Unit
    ) {
        Log.i(TAG, "handle input username = $username, password (md5 hashed) = $password")
        // show progress
        isShowProgress.set(true)
        // hide text view invalid author
        isValid.set(true)
        // action
        if (username.isNotBlank() && password.isNotBlank()) {
            Log.d(TAG, "valid input")
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
                        Log.d(TAG, "handle valid response from Api")
                        callback()
                    }
                } else {
                    Log.d(TAG, "login denied")
                    isValid.set(false)
                    // hide progress
                    isShowProgress.set(false)
                }
            }
        } else {
            Log.d(TAG, "invalid input")
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
        Log.i(TAG, "profile message = ${profileResult.message}")

        if (profileResult.message == AUTHOR_MESSAGE_ERROR) {
            return false
        } else {
            Log.d(TAG, "profile = $profileResult")
            // save profile to local
            Log.d(TAG, "save profile to local")
            saveProfileToLocal(context, profileResult) {
                Log.d(TAG, "save profile successfully")
            }.join()
            // save login state
            Log.d(TAG, "save login state to local")
            saveLoginStateToLocal(context, true) {
                Log.d(TAG, "save login state successfully")
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
        Log.i(TAG, "schedule message = ${scheduleResult.message}")

        if (scheduleResult.message == AUTHOR_MESSAGE_ERROR) {
            return false
        } else {
            Log.d(TAG, "schedule = $scheduleResult")
            // save schedule to database
            Log.d(TAG, "save schedule to database")
            // format start and end time of period
            formatStartEndTime(scheduleResult.periods)
            savePeriodsToDatabase(context, scheduleResult.periods) {
                Log.d(TAG, "save schedule successfully")
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

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "on cleared $TAG")
    }
}