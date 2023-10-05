package com.app.ekma.activities

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.Data
import com.app.ekma.common.GET_SCHEDULE_WORKER_TAG
import com.app.ekma.common.UNIQUE_GET_SCHEDULE_WORK_NAME
import com.app.ekma.data.models.service.ILoginService
import com.app.ekma.data.models.service.INoteService
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.data.models.service.IScheduleService
import com.app.ekma.work.WorkRunner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginService: ILoginService,
    private val noteService: INoteService,
    private val scheduleService: IScheduleService,
    private val profileService: IProfileService
) : BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    fun authForUserEntryApp(
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
                .fromTags(listOf(GET_SCHEDULE_WORKER_TAG))
                .addStates(listOf(WorkInfo.State.FAILED))
                .addUniqueWorkNames(
                    listOf(UNIQUE_GET_SCHEDULE_WORK_NAME)
                )
                .build()
            workManager.getWorkInfos(workQuery)
                .get()
                .forEach { workInfo ->
                    logError("tag=${workInfo.tags} and id=${workInfo.id} with state=${workInfo.state.name}")
                    if (workInfo.tags.contains(GET_SCHEDULE_WORKER_TAG)) {
                        WorkRunner.runGetScheduleWorker(workManager)
                    }
                }
        }
    }

    fun getLocalData() {
        viewModelScope.launch(Dispatchers.IO) {
            Data.getProfile(profileService)
            Data.getLocalData(noteService, scheduleService) {}
        }
    }
}