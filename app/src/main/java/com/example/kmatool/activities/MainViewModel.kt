package com.example.kmatool.activities

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.GET_SCHEDULE_WORKER_TAG
import com.example.kmatool.common.UNIQUE_GET_SCHEDULE_WORK_NAME
import com.example.kmatool.common.UNIQUE_UPDATE_SCHEDULE_WORK_NAME
import com.example.kmatool.common.UPDATE_SCHEDULE_WORKER_TAG
import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IScheduleService
import com.example.kmatool.work.GetScheduleWorkRunner
import com.example.kmatool.work.UpdateScheduleWorkRunner
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

    fun getLocalData(
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Data.getLocalData(noteService, scheduleService, callback)
        }
    }
}