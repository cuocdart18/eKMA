package com.app.ekma.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.ekma.common.CALLING_OPERATION
import com.app.ekma.common.pattern.singleton.CallingOperationResponse

class HangupCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null)
            return
    }
}