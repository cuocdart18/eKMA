package com.example.kmatool.view_model.schedule

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmatool.api_service.ScheduleRepository
import com.example.kmatool.models.schedule.Period
import com.example.kmatool.models.schedule.Profile
import com.example.kmatool.utils.AUTHOR_MESSAGE_ERROR
import com.example.kmatool.utils.AUTHOR_MESSAGE_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class ScheduleLoginViewModel : ViewModel() {
    private val TAG = ScheduleLoginViewModel::class.java.simpleName
    private val scheduleRepository: ScheduleRepository by lazy { ScheduleRepository() }

    // observable field
    var isValid = ObservableField<Boolean>()

    fun handleOnClickBtnLogin(
        username: String,
        password: String,
        callback: () -> Unit
    ) {
        Log.i(TAG, "handle input username = $username, password (md5 hashed) = $password")
        // action
        if (username.isNotBlank() && password.isNotBlank()) {
            Log.d(TAG, "valid input")
            // query api
            makeCallApiScheduleData(username, password, callback)
        } else {
            Log.d(TAG, "invalid input")
            // if invalid input
            isValid.set(false)
        }
    }

    private fun makeCallApiScheduleData(
        username: String,
        password: String,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // call profile
            val profileAsync = async {
                scheduleRepository.getProfile(username, password, true)
            }
            // call schedule data
            val scheduleAsync = async {
                scheduleRepository.getScheduleData(username, password, true)
            }
            val profileResult = profileAsync.await()
            val scheduleResult = scheduleAsync.await()

            Log.i(TAG, "profile message = ${profileResult.message}")
            Log.i(TAG, "schedule data message = ${scheduleResult.message}")

            // if invalid
            if (
                profileResult.message == AUTHOR_MESSAGE_ERROR ||
                scheduleResult.message == AUTHOR_MESSAGE_ERROR
            ) {
                isValid.set(false)
            } else {    // if valid
                Log.d(TAG, "profile = $profileResult")
                Log.d(TAG, "schedule periods = ${scheduleResult.periods}")
                // save profile to local
                val saveProfileAsync = async {
                    saveProfileToLocal(profileResult)
                }
                // save periods data to database
                val savePeriodsAsync = async {
                    savePeriodsToDatabase(scheduleResult.periods)
                }
                saveProfileAsync.await()
                savePeriodsAsync.await()

                // update UI
                withContext(Dispatchers.Main) {
                    callback()
                }
            }
        }
    }

    private fun saveProfileToLocal(profile: Profile) {
        Log.d(TAG, "save profile to local")
        // action
    }

    private fun savePeriodsToDatabase(data: List<Period>) {
        Log.d(TAG, "save periods to database")
        // action
    }
}