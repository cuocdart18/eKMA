package com.app.ekma.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.ekma.common.INCOMING_CALL_ID
import com.app.ekma.common.INPUT_DATA_INVITER_CODE
import com.app.ekma.common.hideNotification
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.firebase.MSG_OPERATION
import com.app.ekma.firebase.MSG_REJECT
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RejectCallWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val fcmService: IFcmService
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = RejectCallWorker::class.java.simpleName

    override suspend fun doWork(): Result {
        rejectCall()
        hideNotification(applicationContext, INCOMING_CALL_ID)
        return Result.success()
    }

    private suspend fun rejectCall() {
        val inviterCode = inputData.getString(INPUT_DATA_INVITER_CODE).toString()
        val senderToken = fcmService.getFcmToken(inviterCode)
        // send data
        val data = mapOf(MSG_OPERATION to MSG_REJECT)
        val fcmDataMessage = FcmDataMessage(senderToken, data)
        fcmService.sendCallInvitationMessage(fcmDataMessage)
        BusyCalling.setData(false)
    }
}