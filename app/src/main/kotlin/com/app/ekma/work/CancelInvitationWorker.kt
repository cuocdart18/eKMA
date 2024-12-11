package com.app.ekma.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.ekma.common.INPUT_DATA_CALL_TYPE
import com.app.ekma.common.INPUT_DATA_RECEIVER_CODES
import com.app.ekma.common.INPUT_DATA_STUDENT_CODE
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.firebase.MSG_CANCEL
import com.app.ekma.firebase.MSG_INVITER_CODE
import com.app.ekma.firebase.MSG_OPERATION
import com.app.ekma.firebase.MSG_TYPE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CancelInvitationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val fcmService: IFcmService
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = CancelInvitationWorker::class.java.simpleName

    override suspend fun doWork(): Result {
        cancelInvitation()
        return Result.success()
    }

    private suspend fun cancelInvitation() {
        val receiverCodes = inputData.getStringArray(INPUT_DATA_RECEIVER_CODES) ?: emptyArray()
        val myStudentCode = inputData.getString(INPUT_DATA_STUDENT_CODE).toString()
        val callType = inputData.getString(INPUT_DATA_CALL_TYPE).toString()

        // get regisToken and send message invitation
        receiverCodes.forEach { code ->
            sendMessageInvitation(code, MSG_CANCEL, myStudentCode, callType)
        }
        BusyCalling.setData(false)
    }

    private suspend fun sendMessageInvitation(
        code: String,
        operation: String,
        myStudentCode: String,
        callType: String
    ) {
        val receiverToken = fcmService.getFcmToken(code)
        // send data
        val data = mapOf(
            MSG_INVITER_CODE to myStudentCode,
            MSG_OPERATION to operation,
            MSG_TYPE to callType
        )
        val fcmDataMessage = FcmDataMessage(receiverToken, data)
        fcmService.sendCallInvitationMessage(fcmDataMessage)
    }
}