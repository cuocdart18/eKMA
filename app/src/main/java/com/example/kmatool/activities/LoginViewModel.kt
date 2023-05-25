package com.example.kmatool.activities

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.alarm.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.common.Resource.Success
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.User
import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.models.service.IScheduleService
import com.example.kmatool.data.models.service.IUserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val dataLocalManager: IDataLocalManager,
    private val noteService: INoteService,
    private val scheduleService: IScheduleService,
    private val loginService: ILoginService,
    private val profileService: IProfileService,
    private val userService: IUserService
) : BaseViewModel() {
    override val TAG = LoginViewModel::class.java.simpleName
    private val DELAY_TIME = 1200L

    // observable field
    var isValid = ObservableField<Boolean>()
    var isShowProgress = ObservableField<Boolean>()

    init {
        isValid.set(true)
        isShowProgress.set(false)
    }

    fun getLoginState(
        callback: () -> Unit
    ) {
        logDebug("get login state")
        isShowProgress.set(true)
        viewModelScope.launch(Dispatchers.IO) {
            val state = loginService.getLoginState()
            delay(DELAY_TIME)
            withContext(Dispatchers.Main) {
                if (state) {
                    callback()
                }
                isShowProgress.set(false)
            }
        }
    }

    fun getLocalData(
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Data.getLocalData(noteService, scheduleService, callback)
        }
    }

    fun handleOnClickBtnLogin(
        username: String,
        password: String,
        callback: (message: String) -> Unit
    ) {
        isShowProgress.set(true)
        isValid.set(true)
        // action
        if (username.isNotBlank() && password.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                val callProfile = async { profileService.getProfile(username, password, true) }
                val callPeriods = async { scheduleService.getPeriods(username, password, true) }
                val profile = callProfile.await()
                val periods = callPeriods.await()

                if (profile is Success && periods is Success) {
                    scheduleService.deletePeriods()                     // delete old periods
                    profileService.saveProfile(profile.data!!)          // save profile to local
                    scheduleService.insertPeriods(periods.data!!)       // save periods to db
                    setAlarmPeriodsInFirstTime(periods.data)            // set periods alarm if possible

                    // for update schedule
                    userService.saveUser(User(username, password, true))

                    loginService.saveLoginState(true)
                    withContext(Dispatchers.Main) {
                        isValid.set(true)
                        isShowProgress.set(false)
                        callback("Login success")
                    }
                } else {
                    isValid.set(false)
                    isShowProgress.set(false)
                    callback("Something went wrong")
                }
            }
        } else {
            isValid.set(false)
            isShowProgress.set(false)
        }
    }

    private suspend fun setAlarmPeriodsInFirstTime(events: List<Event>) {
        if (dataLocalManager.getIsNotifyEvents()) {
            alarmEventsScheduler.scheduleEvents(events)
        }
    }
}