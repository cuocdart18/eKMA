package com.app.ekma.ui.infor.main

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.app.ekma.alarm.AlarmEventsScheduler
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.broadcast_receiver.BootCompletedReceiver
import com.app.ekma.common.Data
import com.app.ekma.common.TedImagePickerStarter
import com.app.ekma.common.saveImageAndGetPath
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.Profile
import com.app.ekma.data.models.service.ILoginService
import com.app.ekma.data.models.service.INoteService
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.data.models.service.IScheduleService
import com.app.ekma.data.models.service.IUserService
import com.app.ekma.work.WorkRunner
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val dataLocalManager: IDataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val scheduleService: IScheduleService,
    private val loginService: ILoginService,
    private val profileService: IProfileService,
    private val userService: IUserService
) : BaseViewModel() {
    override val TAG = InformationViewModel::class.java.simpleName
    private lateinit var profile: Profile
    private lateinit var uri: Uri

    private val _msgToast = MutableLiveData<String>()
    val msgToast: LiveData<String>
        get() = _msgToast

    fun getProfile(
        callback: (profile: Profile) -> Unit
    ) {
        if (this::profile.isInitialized) {
            callback(profile)
            return
        }
        viewModelScope.launch {
            profile = profileService.getProfile()
            callback(profile)
        }
    }

    fun getImageProfile(
        callback: (uri: Uri) -> Unit
    ) {
        if (this::uri.isInitialized) {
            callback(uri)
            return
        }
        viewModelScope.launch {
            val uriString = dataLocalManager.getImgFilePath()
            uri = Uri.parse(uriString)
            callback(uri)
        }
    }

    fun onChangeProfileImage(
        context: Context,
        callback: (uri: Uri) -> Unit
    ) {
        TedImagePickerStarter.startImage(context) { uri ->
            viewModelScope.launch {
                val myStudentCode = profileService.getProfile().studentCode
                // save to local
                this@InformationViewModel.uri = uri
                val filePath = saveImageAndGetPath(context, uri, myStudentCode)
                dataLocalManager.saveImgFilePath(filePath)
                // save to remote by run a worker
                val workerManager = WorkManager.getInstance(context)
                WorkRunner.runUploadAvatarWorker(workerManager, myStudentCode, filePath)
                // UI response
                callback(uri)
            }
        }
    }

    fun updateSchedule(
        context: Context,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val workManager = WorkManager.getInstance(context)
            WorkRunner.runGetScheduleWorker(workManager)
            // update UI: dismiss dialog
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun changedIsNotifyEvents(
        context: Context,
        data: Boolean,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataLocalManager.saveIsNotifyEvents(data)
            if (data) {
                alarmEventsScheduler.scheduleAlarmEvents()
                val receiver = ComponentName(context, BootCompletedReceiver::class.java)
                context.packageManager.setComponentEnabledSetting(
                    receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            } else {
                alarmEventsScheduler.clearAlarmEvents()
                val receiver = ComponentName(context, BootCompletedReceiver::class.java)
                context.packageManager.setComponentEnabledSetting(
                    receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
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
            val clearAlarm = launch {
                if (dataLocalManager.getIsNotifyEvents())
                    alarmEventsScheduler.clearAlarmEvents()
            }
            val clearProfile = launch { profileService.clearProfile() }
            val clearUser = launch { userService.clearUser() }
            val clearImage = launch { dataLocalManager.saveImgFilePath("") }
            val clearLoginState = launch { loginService.saveLoginState(false) }
            clearPeriods.join()
            clearAlarm.join()
            clearProfile.join()
            clearUser.join()
            clearImage.join()
            clearLoginState.join()

            // clear cache memory
            Data.myStudentInfo = null
            Data.profile = Profile("", "", "", "")
            Data.saveDateClicked = CalendarDay(LocalDate.now(), DayPosition.MonthDate)

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}