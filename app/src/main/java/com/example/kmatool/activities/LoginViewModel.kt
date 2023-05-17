package com.example.kmatool.activities

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.data.data_source.app_data.DataLocalManager
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.models.service.IScheduleService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val dataLocalManager: DataLocalManager,
    private val noteService: INoteService,
    private val scheduleService: IScheduleService,
    private val loginService: ILoginService,
    private val profileService: IProfileService
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
            val job1 = launch { getLocalPeriodsRuntime() }
            val job2 = launch { getLocalNotesRuntime() }
            job1.join()
            job2.join()
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    private suspend fun getLocalPeriodsRuntime() {
        val result = scheduleService.getPeriods()
        withContext(Dispatchers.Default) {
            Data.periodsDayMap =
                result.groupBy { it.day } as MutableMap<String, List<Period>>
            // sort periods on a day by startTime
            sortPeriodsDayByStartTime()
        }
    }

    private fun sortPeriodsDayByStartTime() {
        Data.periodsDayMap.forEach { (t, u) ->
            Data.periodsDayMap[t] = u.sortedBy { it.startTime }
        }
    }

    private suspend fun getLocalNotesRuntime() {
        val result = noteService.getNotes()
        withContext(Dispatchers.Default) {
            Data.notesDayMap =
                result.groupBy { it.date } as MutableMap<String, List<Note>>
            // sort notes on a day by day
            sortNotesDayByTime()
        }
    }

    private fun sortNotesDayByTime() {
        Data.notesDayMap.forEach { (t, u) ->
            Data.notesDayMap[t] = u.sortedBy { it.time }
        }
    }

    fun handleOnClickBtnLogin(
        username: String,
        password: String,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataLocalManager.saveUsername(username)
            dataLocalManager.savePassword(password)
        }

        isShowProgress.set(true)
        isValid.set(true)
        // action
        if (username.isNotBlank() && password.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                // call and save profile
                val callProfile = async { profileService.getProfile(username, password, true) }
                profileService.saveProfile(callProfile.await())

                // call schedule -> set alarm if possible
                val callPeriods = async { scheduleService.getPeriods(username, password, true) }
                scheduleService.deletePeriods()
                setAlarmPeriodsInFirstTime(callPeriods.await())
                scheduleService.insertPeriods(callPeriods.await())

                callProfile.join()
                callPeriods.join()

                loginService.saveLoginState(true)

                withContext(Dispatchers.Main) {
                    isValid.set(true)
                    isShowProgress.set(false)
                    callback()
                }
            }
        } else {
            isValid.set(false)
            isShowProgress.set(false)
        }
    }

    private suspend fun setAlarmPeriodsInFirstTime(events: List<Event>) {
        if (dataLocalManager.getIsNotifyEventsSPref()) {
            alarmEventsScheduler.scheduleEvents(events)
        }
    }
}