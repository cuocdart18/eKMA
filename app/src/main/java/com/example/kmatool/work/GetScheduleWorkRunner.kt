package com.example.kmatool.work

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kmatool.common.GET_SCHEDULE_WORKER_TAG
import com.example.kmatool.common.UNIQUE_GET_SCHEDULE_WORK_NAME

object GetScheduleWorkRunner {

    fun run(workManager: WorkManager) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val getScheduleWorkRequest =
            OneTimeWorkRequestBuilder<GetScheduleWorker>()
                .addTag(GET_SCHEDULE_WORKER_TAG)
                .setConstraints(constraints)
                .build()
        workManager
            .enqueueUniqueWork(
                UNIQUE_GET_SCHEDULE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                getScheduleWorkRequest
            )
    }
}