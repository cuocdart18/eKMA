package com.example.kmatool.ui.infor.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.common.FileUtils
import com.example.kmatool.common.TedImagePickerStarter
import com.example.kmatool.common.jsonStringToObject
import com.example.kmatool.data.data_source.app_data.DataLocalManager
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.models.service.IScheduleService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val dataLocalManager: DataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val noteService: INoteService,
    private val scheduleService: IScheduleService,
    private val loginService: ILoginService,
    private val profileService: IProfileService
) : BaseViewModel() {

    private lateinit var profile: Profile
    private lateinit var uri: Uri

    fun getProfile(
        callback: (profile: Profile) -> Unit
    ) {
        if (this::profile.isInitialized) {
            callback(profile)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            profile = jsonStringToObject(dataLocalManager.getProfileSPref())
            CoroutineScope(Dispatchers.Main).launch {
                callback(profile)
            }
        }
    }

    fun getImageProfile(
        callback: (uri: Uri) -> Unit
    ) {
        if (this::uri.isInitialized) {
            callback(uri)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val uriString = dataLocalManager.getImgFilePathSPref()
            uri = Uri.parse(uriString)
            withContext(Dispatchers.Main) {
                callback(uri)
            }
        }
    }

    fun onChangeProfileImage(
        context: Context,
        callback: (uri: Uri) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            TedImagePickerStarter.startImage(context) { uri ->
                CoroutineScope(Dispatchers.IO).launch {
                    val filePath = FileUtils.saveImageAndGetPath(context, uri)
                    dataLocalManager.saveImgFilePathSPref(filePath)
                    withContext(Dispatchers.Main) {
                        this@InformationViewModel.uri = uri
                        callback(uri)
                    }
                }
            }
        }
    }

    fun updateSchedule(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val username = dataLocalManager.getUsername()
            val password = dataLocalManager.getPassword()
            // call API
            val callPeriods = async { scheduleService.getPeriods(username, password, true) }
            scheduleService.deletePeriods()
            setAlarmPeriodsInFirstTime(callPeriods.await())
            scheduleService.insertPeriods(callPeriods.await())

            callPeriods.join()

            // cancel alarm periods
            if (dataLocalManager.getIsNotifyEventsSPref()) {
                alarmEventsScheduler.cancelPeriods()
            }
            // update Data runtime
            getLocalPeriodsRuntime()
            // update UI: dismiss dialog
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    private suspend fun setAlarmPeriodsInFirstTime(events: List<Event>) {
        if (dataLocalManager.getIsNotifyEventsSPref()) {
            alarmEventsScheduler.scheduleEvents(events)
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

    private fun sortNotesDayByTime() {
        Data.notesDayMap.forEach { (t, u) ->
            Data.notesDayMap[t] = u.sortedBy { it.time }
        }
    }

    fun changedIsNotifyEvents(
        data: Boolean,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataLocalManager.saveIsNotifyEventsSPref(data)
            if (data) {
                alarmEventsScheduler.scheduleAlarmEvents()
            } else {
                alarmEventsScheduler.clearAlarmEvents()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun signOut(
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val clearPeriods = launch { scheduleService.deletePeriods() }
            val clearNotes = launch { noteService.deleteNotes() }
            val clearAlarm = launch {
                if (dataLocalManager.getIsNotifyEventsSPref())
                    alarmEventsScheduler.clearAlarmEvents()
            }
            val clearProfile = launch { profileService.clearProfile() }
            val clearUsername = launch { dataLocalManager.saveUsername("") }
            val clearPassword = launch { dataLocalManager.savePassword("") }
            val clearImage = launch { dataLocalManager.saveImgFilePathSPref("") }
            val clearLoginState = launch { loginService.saveLoginState(false) }

            clearPeriods.join()
            clearNotes.join()
            clearAlarm.join()
            clearProfile.join()
            clearUsername.join()
            clearPassword.join()
            clearImage.join()
            clearLoginState.join()

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}