package com.app.ekma.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.ekma.common.INCOMING_CALL_ID
import com.app.ekma.common.PENDING_RESPONSE_TIME
import com.app.ekma.common.hideNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class HideNotificationPendingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = HideNotificationPendingWorker::class.java.simpleName

    override suspend fun doWork(): Result {
        try {
            delay(PENDING_RESPONSE_TIME)
            hideNotification(applicationContext, INCOMING_CALL_ID)
        } catch (e: Exception) {
            return Result.failure()
        }
        return Result.success()
    }
}