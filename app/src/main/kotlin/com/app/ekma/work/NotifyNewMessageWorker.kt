package com.app.ekma.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.ekma.common.INPUT_DATA_NEW_MSG
import com.app.ekma.common.INPUT_DATA_RECEIVER_CODES
import com.app.ekma.common.NOTIFY_NEW_MSG_TIME
import com.app.ekma.common.jsonObjectToString
import com.app.ekma.common.jsonStringToObject
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.firebase.MSG_DATA
import com.app.ekma.firebase.MSG_NEW_MESSAGE
import com.app.ekma.firebase.MSG_OPERATION
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotifyNewMessageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val fcmService: IFcmService
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = NotifyNewMessageWorker::class.java.simpleName

    override suspend fun doWork(): Result {
        notifyNewMessage()
        return Result.success()
    }

    private suspend fun notifyNewMessage() {
        val receiverCodes = inputData.getStringArray(INPUT_DATA_RECEIVER_CODES) ?: emptyArray()
        val messageData = inputData.getString(INPUT_DATA_NEW_MSG).toString()

        val dataMap = jsonStringToObject<MutableMap<String, Any>>(messageData).apply {
            put(NOTIFY_NEW_MSG_TIME, System.currentTimeMillis())
        }

        val data = mapOf(
            MSG_OPERATION to MSG_NEW_MESSAGE,
            MSG_DATA to jsonObjectToString(dataMap)
        )
        receiverCodes.forEach {
            val receiverToken = fcmService.getFcmToken(it)
            val fcmDataMessage = FcmDataMessage(receiverToken, data)
            fcmService.sendNewMessage(fcmDataMessage)
        }
    }
}