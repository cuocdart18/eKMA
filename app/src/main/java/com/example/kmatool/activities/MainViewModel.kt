package com.example.kmatool.activities

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.UNIQUE_SCHEDULE_WORK_NAME
import com.example.kmatool.common.UPDATE_SCHEDULE_WORKER_TAG
import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IScheduleService
import com.example.kmatool.work.UpdateScheduleWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginService: ILoginService,
    private val noteService: INoteService,
    private val scheduleService: IScheduleService
) : BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    fun authForUserEntryAppFromDeepLink(
        callback: (state: Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = loginService.getLoginState()
            withContext(Dispatchers.Main) {
                callback(state)
            }
        }
    }

    fun runWorkerIfFailure(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val workManager = WorkManager.getInstance(context)
            workManager.getWorkInfosByTag(UPDATE_SCHEDULE_WORKER_TAG)
                .get()
                .forEach { workInfo ->
                    logError("tag=${workInfo.tags} with state=${workInfo.state.name}")
                    if (workInfo.state == WorkInfo.State.FAILED) {
                        val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                        val updateWorkRequest =
                            OneTimeWorkRequestBuilder<UpdateScheduleWorker>()
                                .addTag(UPDATE_SCHEDULE_WORKER_TAG)
                                .setConstraints(constraints)
                                .build()
                        workManager
                            .enqueueUniqueWork(
                                UNIQUE_SCHEDULE_WORK_NAME,
                                ExistingWorkPolicy.REPLACE,
                                updateWorkRequest
                            )
                    }
                }
        }
    }

    fun getLocalData(
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Data.getLocalData(noteService, scheduleService, callback)
        }
    }
}