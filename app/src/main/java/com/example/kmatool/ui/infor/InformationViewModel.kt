package com.example.kmatool.ui.infor

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.DataStoreManager
import com.example.kmatool.common.FileUtils
import com.example.kmatool.common.TedImagePickerStarter
import com.example.kmatool.common.jsonStringToObject
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.repositories.NoteRepository
import com.example.kmatool.data.repositories.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val noteRepository: NoteRepository,
    private val scheduleRepository: ScheduleRepository
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
            dataStoreManager.profileDataStoreFlow.collect() {
                profile = jsonStringToObject(it)
                withContext(Dispatchers.Main) {
                    logDebug("get profile=$profile")
                    callback(profile)
                }
                cancel()
            }
        }
    }

    fun getImageProfile(
        callback: (uri: Uri) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.imgFilePathDataStoreFlow.collect() { filePath ->
                val file = File(filePath)
                if (file.exists()) {
                    withContext(Dispatchers.Main) {
                        val uri = Uri.fromFile(file)
                        logDebug("uri=$uri")
                        callback(uri)
                    }
                }
                cancel()
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
                    dataStoreManager.storeImgFilePath(filePath)
                    withContext(Dispatchers.Main) {
                        callback(uri)
                    }
                }
            }
        }
    }

    fun storeIsNotifyEvents(
        data: Boolean,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.storeIsNotifyEvents(data)
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
            val clearProfile = launch { dataStoreManager.storeProfile("") }
            val clearImage = launch { dataStoreManager.storeImgFilePath("") }
            val clearNotifyEvent = launch { dataStoreManager.storeIsNotifyEvents(false) }
            val clearLoginState = launch { dataStoreManager.storeIsLogin(false) }
            val clearDataRuntime = launch {
                Data.notesDayMap.clear()
                Data.periodsDayMap.clear()
            }

            clearPeriods.join()
            clearNotes.join()
            clearProfile.join()
            clearImage.join()
            clearNotifyEvent.join()
            clearLoginState.join()
            clearDataRuntime.join()

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}