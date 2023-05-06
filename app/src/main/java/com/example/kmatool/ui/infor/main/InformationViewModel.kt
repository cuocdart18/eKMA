package com.example.kmatool.ui.infor.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.DataLocalManager
import com.example.kmatool.common.FileUtils
import com.example.kmatool.common.TedImagePickerStarter
import com.example.kmatool.common.jsonStringToObject
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.repositories.NoteRepository
import com.example.kmatool.data.repositories.ScheduleRepository
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
    private val noteRepository: NoteRepository,
    private val scheduleRepository: ScheduleRepository,
    private val alarmEventsScheduler: AlarmEventsScheduler
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
            val result = async { scheduleRepository.callScheduleApi(username, password) }
            if (result.await()) {
                // cancel alarm periods
                if (dataLocalManager.getIsNotifyEventsSPref()) {
                    alarmEventsScheduler.cancelPeriods()
                }
                // update Data runtime
                scheduleRepository.getLocalPeriodsRuntime()
                // update UI: dismiss dialog
                withContext(Dispatchers.Main) {
                    callback()
                }
            }
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
            val clearPeriods = launch { scheduleRepository.deletePeriods() }
            val clearNotes = launch { noteRepository.deleteNotes() }
            val clearAlarm = launch {
                if (dataLocalManager.getIsNotifyEventsSPref())
                    alarmEventsScheduler.clearAlarmEvents()
            }
            val clearProfile = launch { dataLocalManager.saveProfileSPref("") }
            val clearUsername = launch { dataLocalManager.saveUsername("") }
            val clearPassword = launch { dataLocalManager.savePassword("") }
            val clearImage = launch { dataLocalManager.saveImgFilePathSPref("") }
            val clearLoginState = launch { dataLocalManager.saveLoginStateSPref(false) }

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