package com.example.kmatool.work

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kmatool.common.UNIQUE_UPDATE_SCHEDULE_WORK_NAME
import com.example.kmatool.common.UPDATE_SCHEDULE_WORKER_TAG

object UpdateScheduleWorkRunner {

    fun run(workManager: WorkManager) {
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
                UNIQUE_UPDATE_SCHEDULE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                updateWorkRequest
            )
    }
}