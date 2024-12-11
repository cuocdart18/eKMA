package com.app.ekma.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.ekma.common.INCOMING_CALL_ID
import com.app.ekma.common.hideNotification

class AnswerCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null)
            return
        Log.e("AnswerCallReceiver", "onReceive: cuocdat")
        hideNotification(context, INCOMING_CALL_ID)
    }
}