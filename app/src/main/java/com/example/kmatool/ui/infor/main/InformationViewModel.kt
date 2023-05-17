package com.example.kmatool.ui.infor.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.common.FileUtils
import com.example.kmatool.common.TedImagePickerStarter
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.models.service.IScheduleService
import com.example.kmatool.data.models.service.IUserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val dataLocalManager: IDataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val noteService: INoteService,
    private val scheduleService: IScheduleService,
    private val loginService: ILoginService,
    private val profileService: IProfileService,
    private val userService: IUserService
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
            profile = profileService.getProfile()
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
            val uriString = dataLocalManager.getImgFilePath()
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
                    dataLocalManager.saveImgFilePath(filePath)
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
            val user = userService.getUser()
            // call API
            val callPeriods =
                async { scheduleService.getPeriods(user.username, user.password, user.hashed) }

            scheduleService.deletePeriods()
            setAlarmPeriodsInFirstTime(callPeriods.await())
            scheduleService.insertPeriods(callPeriods.await())

            // cancel alarm periods
            if (dataLocalManager.getIsNotifyEvents()) {
                alarmEventsScheduler.cancelPeriods()
            }
            // update Data runtime
            Data.getLocalPeriodsRuntime(scheduleService)
            // update UI: dismiss dialog
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    private suspend fun setAlarmPeriodsInFirstTime(events: List<Event>) {
        if (dataLocalManager.getIsNotifyEvents()) {
            alarmEventsScheduler.scheduleEvents(events)
        }
    }

    fun changedIsNotifyEvents(
        data: Boolean,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataLocalManager.saveIsNotifyEvents(data)
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
                if (dataLocalManager.getIsNotifyEvents())
                    alarmEventsScheduler.clearAlarmEvents()
            }
            val clearProfile = launch { profileService.clearProfile() }
            val clearUser = launch { userService.clearUser() }
            val clearImage = launch { dataLocalManager.saveImgFilePath("") }
            val clearLoginState = launch { loginService.saveLoginState(false) }

            clearPeriods.join()
            clearNotes.join()
            clearAlarm.join()
            clearProfile.join()
            clearUser.join()
            clearImage.join()
            clearLoginState.join()

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}