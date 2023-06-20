package com.example.kmatool.activities

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.alarm.AlarmEventsScheduler
import com.example.kmatool.common.AUTH_MESSAGE_SUCCESS
import com.example.kmatool.common.Resource.Success
import com.example.kmatool.common.md5
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.User
import com.example.kmatool.data.models.service.ILoginService
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
    private val scheduleService: IScheduleService,
    private val loginService: ILoginService,
    private val profileService: IProfileService,
    private val userService: IUserService
) : BaseViewModel() {
    override val TAG = LoginViewModel::class.java.simpleName

    // observable field
    var isValid = ObservableField<Boolean>()
    var isShowProgress = ObservableField<Boolean>()

    init {
        isValid.set(true)
        isShowProgress.set(false)
    }

    fun handleOnClickBtnLogin(
        username: String,
        unHashedPassword: String,
        callback: () -> Unit
    ) {
        isValid.set(true)

        // action
        if (username.isNotBlank() && unHashedPassword.isNotBlank()) {
            // show for user: aware of request
            isShowProgress.set(true)

            val password = md5(unHashedPassword)
            viewModelScope.launch(Dispatchers.IO) {
                val callAuth = loginService.auth(username, password, true)

                if (callAuth is Success && callAuth.data.equals(AUTH_MESSAGE_SUCCESS)) {
                    // get schedule and profile
                    val callProfile = async { profileService.getProfile(username, password, true) }
                    val callPeriods = async { scheduleService.getPeriods(username, password, true) }
                    val profile = callProfile.await()
                    val periods = callPeriods.await()

                    if (profile is Success) {
                        scheduleService.deletePeriods()                                  // delete old periods
                        profile.data?.let { profileService.saveProfile(it) }            // save profile to local
                        periods.data?.let {
                            scheduleService.insertPeriods(it)                           // save periods to db
                            setAlarmPeriodsInFirstTime(it)                             // set periods alarm if possible
                        }
                        userService.saveUser(User(username, password, true))
                        loginService.saveLoginState(true)
                        withContext(Dispatchers.Main) {
                            isValid.set(true)
                            isShowProgress.set(false)
                            callback()
                        }
                    } else {
                        showLoginRefused()      // if backend unavailable
                    }
                } else {
                    showLoginRefused()      // if get or auth failed
                }
            }
        } else {
            showLoginRefused()      // if invalid input
        }
    }

    private fun showLoginRefused() {
        isValid.set(false)
        isShowProgress.set(false)
    }

    private suspend fun setAlarmPeriodsInFirstTime(events: List<Event>) {
        if (dataLocalManager.getIsNotifyEvents()) {
            alarmEventsScheduler.scheduleEvents(events)
        }
    }
}