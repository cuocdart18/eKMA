package com.example.kmatool.ui.infor.main

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.alarm.AlarmEventsScheduler
import com.example.kmatool.broadcast_receiver.BootCompletedReceiver
import com.example.kmatool.common.AVATAR_FILE
import com.example.kmatool.common.Data
import com.example.kmatool.common.FileUtils
import com.example.kmatool.common.TedImagePickerStarter
import com.example.kmatool.common.USERS_DIR
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.models.service.IScheduleService
import com.example.kmatool.data.models.service.IUserService
import com.example.kmatool.firebase.storage
import com.example.kmatool.work.GetScheduleWorkRunner
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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
    override val TAG = InformationViewModel::class.java.simpleName
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
            withContext(Dispatchers.Main) {
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
            val myStudentCode = profileService.getProfile().studentCode
            TedImagePickerStarter.startImage(context) { uri ->
                CoroutineScope(Dispatchers.IO).launch {
                    val filePath = FileUtils.saveImageAndGetPath(context, uri)
                    // save to local
                    dataLocalManager.saveImgFilePath(filePath)
                    // save to storage
                    val avtRef = storage.child("$USERS_DIR/$myStudentCode/$AVATAR_FILE")
                    avtRef.putFile(uri).addOnSuccessListener {
                        avtRef.downloadUrl.addOnCompleteListener { uri ->
                            val url = uri.result.toString()
                            logError("link=$url")
                        }
                    }.addOnFailureListener { logError("$it") }
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
            GetScheduleWorkRunner.run(workManager)
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
            val clearNotes = launch {
                launch {
                    Data.notesDayMap.forEach {
                        launch {
                            it.value.forEach {
                                launch {
                                    File(it.audioPath.toString()).delete()
                                    logError("delete ${it.audioPath.toString()} done")
                                }
                            }
                        }
                    }
                }
                noteService.deleteNotes()
            }
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
            Data.profile = Profile("", "", "", "")
            Data.saveDateClicked = CalendarDay(LocalDate.now(), DayPosition.MonthDate)

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}