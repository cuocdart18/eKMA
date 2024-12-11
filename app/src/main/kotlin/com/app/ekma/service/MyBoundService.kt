package com.app.ekma.service

import android.content.Intent
import android.os.Binder
import com.app.ekma.base.service.BaseService

class MyBoundService : BaseService() {
    override val TAG = MyBoundService::class.java.simpleName

    inner class LocalBinder : Binder() {
        fun getService(): MyBoundService = this@MyBoundService
    }

    override fun getBinder() = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}