package com.app.ekma.base.service

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.IBinder
import android.util.Log

abstract class BaseService : Service() {
    protected open val TAG = ""

    inner class LocalBinder : Binder() {
        fun getService(): BaseService = this@BaseService
    }

    fun getCurrentTime(): String {
        return System.currentTimeMillis().toString()
    }

    abstract fun getBinder(): Binder
    override fun onBind(intent: Intent?): IBinder? {
        logLifecycle("onBind")
        return getBinder()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        logLifecycle("onConfigurationChanged")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        logLifecycle("onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        logLifecycle("onTrimMemory")
    }

    override fun onCreate() {
        super.onCreate()
        logLifecycle("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logLifecycle("onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifecycle("onDestroy")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        logLifecycle("onUnbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        logLifecycle("onRebind")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        logLifecycle("onTaskRemoved")
    }

    override fun onTimeout(startId: Int) {
        super.onTimeout(startId)
        logLifecycle("onTimeout")
    }

    // LOG
    open fun logLifecycle(msg: String) {
        Log.d(TAG, msg)
    }

    open fun logError(msg: String) {
        Log.e(TAG, msg)
    }

    open fun logDebug(msg: String) {
        Log.d(TAG, msg)
    }

    open fun logInfo(msg: String) {
        Log.i(TAG, msg)
    }

    open fun logWarning(msg: String) {
        Log.w(TAG, msg)
    }
}