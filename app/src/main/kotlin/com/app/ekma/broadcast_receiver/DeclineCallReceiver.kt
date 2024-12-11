package com.app.ekma.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.app.ekma.common.INCOMING_CALL_WORKER_NAME
import com.app.ekma.firebase.MSG_INVITER_CODE
import com.app.ekma.firebase.MSG_TYPE
import com.app.ekma.work.WorkRunner

class DeclineCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null)
            return

        intent.extras?.let {
            val inviterCode = it.getString(MSG_INVITER_CODE, "")
            val callType = it.getString(MSG_TYPE, "")
            WorkManager.getInstance(context).let { workManager ->
                workManager.cancelUniqueWork(INCOMING_CALL_WORKER_NAME)
                WorkRunner.runRejectCallWorker(workManager, inviterCode, callType)
            }
        }
    }
}