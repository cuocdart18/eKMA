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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = dataLocalManager.getImgFilePathSPref()
            val file = File(filePath)
            if (file.exists()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val uri = Uri.fromFile(file)
                    logDebug("uri=$uri")
                    callback(uri)
                }
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
                    // convert uri to file path and save it
                    val filePath = FileUtils.getRealPathFromURI(context, uri)
                    dataLocalManager.saveImgFilePathSPref(filePath)
                    withContext(Dispatchers.Main) {
                        callback(uri)
                    }
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
            val clearPeriods = launch { scheduleRepository.deletePeriods { } }
            val clearNotes = launch { noteRepository.deleteNotes { } }
            val clearAlarm = launch {
                if (dataLocalManager.getIsNotifyEventsSPref())
                    alarmEventsScheduler.clearAlarmEvents()
            }
            val clearProfile = launch { dataLocalManager.saveProfileSPref("") }
            val clearImage = launch { dataLocalManager.saveImgFilePathSPref("") }
            val clearLoginState = launch { dataLocalManager.saveLoginStateSPref(false) }

            clearPeriods.join()
            clearNotes.join()
            clearAlarm.join()
            clearProfile.join()
            clearImage.join()
            clearLoginState.join()

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}