package com.example.kmatool.view_model.schedule

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmatool.api_service.ScheduleRepository
import com.example.kmatool.models.schedule.Period
import com.example.kmatool.utils.AUTHOR_MESSAGE_ERROR
import com.example.kmatool.utils.AUTHOR_MESSAGE_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            val result = scheduleRepository.getScheduleData(username, password, true)
            Log.i(TAG, "login result = ${result.message}")

            // if invalid
            if (result.message == AUTHOR_MESSAGE_ERROR) {
                isValid.set(false)
            } else if (result.message == AUTHOR_MESSAGE_SUCCESS) {    // if valid
                Log.d(TAG, "schedule periods = ${result.periods}")
                // save data to database
                savePeriodsToDatabase(result.periods)
                // update UI
                withContext(Dispatchers.Main) {
                    callback()
                }
            }
        }
    }

    private fun savePeriodsToDatabase(data: List<Period>?) {
        Log.d(TAG, "save periods to database")
        // action
    }
}