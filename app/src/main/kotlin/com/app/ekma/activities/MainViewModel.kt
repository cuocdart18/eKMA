package com.app.ekma.activities

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.firebase.AVATAR_FILE
import com.app.ekma.common.Data
import com.app.ekma.common.GET_SCHEDULE_WORKER_TAG
import com.app.ekma.common.UNIQUE_GET_SCHEDULE_WORK_NAME
import com.app.ekma.common.UNIQUE_UPDATE_SCHEDULE_WORK_NAME
import com.app.ekma.common.UPDATE_SCHEDULE_WORKER_TAG
import com.app.ekma.firebase.USERS_DIR
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.service.ILoginService
import com.app.ekma.data.models.service.INoteService
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.data.models.service.IScheduleService
import com.app.ekma.firebase.storage
import com.app.ekma.work.GetScheduleWorkRunner
import com.app.ekma.work.UpdateScheduleWorkRunner
import com.google.firebase.storage.StorageException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginService: ILoginService,
    private val noteService: INoteService,
    private val scheduleService: IScheduleService,
    private val profileService: IProfileService,
    private val dataLocalManager: IDataLocalManager
) : BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    fun authForUserEntryAppFromDeepLink(
        callback: (state: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val state = loginService.getLoginState()
            callback(state)
        }
    }

    fun runWorkerIfFailure(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val workManager = WorkManager.getInstance(context)
            val workQuery = WorkQuery.Builder
                .fromTags(listOf(UPDATE_SCHEDULE_WORKER_TAG, GET_SCHEDULE_WORKER_TAG))
                .addStates(listOf(WorkInfo.State.FAILED))
                .addUniqueWorkNames(
                    listOf(UNIQUE_UPDATE_SCHEDULE_WORK_NAME, UNIQUE_GET_SCHEDULE_WORK_NAME)
                )
                .build()
            workManager.getWorkInfos(workQuery)
                .get()
                .forEach { workInfo ->
                    logError("tag=${workInfo.tags} and id=${workInfo.id} with state=${workInfo.state.name}")
                    if (workInfo.tags.contains(UPDATE_SCHEDULE_WORKER_TAG)) {
                        UpdateScheduleWorkRunner.run(workManager)
                    }
                    if (workInfo.tags.contains(GET_SCHEDULE_WORKER_TAG)) {
                        GetScheduleWorkRunner.run(workManager)
                    }
                }
        }
    }

    fun getLocalData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            Data.getProfile(profileService)
            Data.getLocalData(noteService, scheduleService) {}

            // check exist avatar local file
            val imgPath = dataLocalManager.getImgFilePath()
            if (imgPath.isEmpty()) {
                logError(imgPath)
                val file = File(context.filesDir, AVATAR_FILE)
                downloadAvatarFromStorage(file)
            } else {
                logError(imgPath)
                compareTotalBytesInLocalWithStorage(imgPath)
            }
        }
    }

    private suspend fun compareTotalBytesInLocalWithStorage(imgPath: String) =
        withContext(Dispatchers.IO) {
            val file = File(imgPath)
            val myStudentCode = profileService.getProfile().studentCode
            val storageReference = storage.child("$USERS_DIR/$myStudentCode/$AVATAR_FILE")
            storageReference.metadata.addOnSuccessListener { data ->
                val remoteTotalBytes = data.sizeBytes
                val localTotalBytes = file.length()
                logError("remote=$remoteTotalBytes and local=$localTotalBytes")
                if (localTotalBytes != remoteTotalBytes) {
                    viewModelScope.launch {
                        downloadAvatarFromStorage(file)
                    }
                }
            }.addOnFailureListener {
                it as StorageException
                logError(it.toString())
            }
        }

    private suspend fun downloadAvatarFromStorage(file: File) =
        withContext(Dispatchers.IO) {
            val myStudentCode = profileService.getProfile().studentCode
            val storageReference = storage.child("$USERS_DIR/$myStudentCode/$AVATAR_FILE")
            storageReference.getFile(file).addOnSuccessListener {
                logError("Download avatar successfully")
                viewModelScope.launch {
                    dataLocalManager.saveImgFilePath(file.absolutePath)
                }
            }.addOnFailureListener {
                it as StorageException
                logError(it.toString())
            }
        }
}