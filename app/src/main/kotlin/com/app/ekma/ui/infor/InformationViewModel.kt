package com.app.ekma.ui.infor

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.app.ekma.alarm.AlarmEventsScheduler
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.broadcast_receiver.BootCompletedReceiver
import com.app.ekma.common.TedImagePickerStarter
import com.app.ekma.common.pattern.singleton.ClickedDay
import com.app.ekma.common.pattern.singleton.ConnReferenceKey
import com.app.ekma.common.pattern.singleton.CurrentEventsRefresher
import com.app.ekma.common.pattern.singleton.MainBottomNavigation
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.pattern.singleton.StudentScoreSingleton
import com.app.ekma.common.saveImageAndGetPath
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.ProfileDetail
import com.app.ekma.data.models.service.ILoginService
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.data.models.service.IScheduleService
import com.app.ekma.data.models.service.IUserService
import com.app.ekma.firebase.ACTIVE_STATUS
import com.app.ekma.firebase.CONNECTIONS
import com.app.ekma.firebase.LAST_ONLINE
import com.app.ekma.firebase.database
import com.app.ekma.work.WorkRunner
import com.google.firebase.database.ServerValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private lateinit var uri: Uri

    private val _profileDetail = MutableStateFlow<ProfileDetail?>(null)
    val profileDetail: StateFlow<ProfileDetail?>
        get() = _profileDetail.asStateFlow()

    fun getProfileDetail() {
        viewModelScope.launch {
            profileService.getProfileDetail().collect {
                _profileDetail.value = it
            }
        }
    }

    fun getImageProfile(callback: (uri: Uri) -> Unit) {
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

    fun onChangeProfileImage(context: Context, callback: (uri: Uri) -> Unit) {
        TedImagePickerStarter.startImage(context) { uri ->
            viewModelScope.launch {
                val myStudentCode = ProfileSingleton().studentCode
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

    fun updateSchedule(context: Context, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val workManager = WorkManager.getInstance(context)
            WorkRunner.runGetScheduleWorker(workManager)
            // update UI: dismiss dialog
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun getIsNotifyEvent(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(dataLocalManager.getIsNotifyEvents())
        }
    }

    fun changedIsNotifyEvents(context: Context, data: Boolean) {
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
        }
    }

    fun signOut(context: Context, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val myStudentCode = ProfileSingleton().studentCode
            // change active status
            if (ConnReferenceKey().isNotEmpty()) {
                database.child(ACTIVE_STATUS).child(myStudentCode).child(LAST_ONLINE)
                    .setValue(ServerValue.TIMESTAMP)
                database.child(ACTIVE_STATUS).child(myStudentCode).child(CONNECTIONS)
                    .child(ConnReferenceKey()).removeValue()
            }

            // cancel running worker
            WorkManager.getInstance(context).cancelAllWork()

            // clear disk memory
            val clearPeriods = launch { scheduleService.deletePeriods() }
            val clearAlarm = launch {
                if (dataLocalManager.getIsNotifyEvents())
                    alarmEventsScheduler.clearAlarmEvents()
            }
            val clearProfile = launch { profileService.clearProfile(myStudentCode) }
            val clearUser = launch { userService.clearUser() }
            val clearImage = launch { dataLocalManager.saveImgFilePath("") }
            val clearLoginState = launch { loginService.saveLoginState(false) }
            clearPeriods.join()
            clearAlarm.join()
            clearProfile.join()
            clearUser.join()
            clearImage.join()
            clearLoginState.join()

            // clear cached memory
            StudentScoreSingleton.release()
            ProfileSingleton.release()
            ConnReferenceKey.release()
            ClickedDay.release()
            CurrentEventsRefresher.release()
            MainBottomNavigation.release()

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}