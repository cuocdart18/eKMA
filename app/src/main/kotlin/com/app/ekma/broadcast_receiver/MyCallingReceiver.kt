package com.app.ekma.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.ekma.common.CALLING_OPERATION
import com.app.ekma.common.pattern.singleton.CallingOperationResponse

class MyCallingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null)
            return

        val data = intent.getStringExtra(CALLING_OPERATION) ?: ""
        CallingOperationResponse.setData(data)
    }
}