package com.example.kmatool.ui.infor.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.alarm.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.common.FileUtils
import com.example.kmatool.common.TedImagePickerStarter
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.models.service.IScheduleService
import com.example.kmatool.data.models.service.IUserService
import com.example.kmatool.work.UpdateScheduleWorkRunner
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
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

    fun updateSchedule(
        context: Context,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val workManager = WorkManager.getInstance(context)
            UpdateScheduleWorkRunner.run(workManager)
            // update UI: dismiss dialog
            withContext(Dispatchers.Main) {
                callback()
            }
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
        context: Context,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // cancel running worker
            WorkManager.getInstance(context).cancelAllWork()

            // clear disk memory
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

            // clear cache memory
            Data.myStudentInfo = null
            Data.saveDateClicked = CalendarDay(LocalDate.now(), DayPosition.MonthDate)

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}