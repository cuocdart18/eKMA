package com.example.kmatool.base.repositories

import android.util.Log

open class BaseRepositories {
    protected open val TAG = ""

    // LOG
    open fun logLifecycle(msg: String) {
        Log.d(TAG, "$msg $TAG")
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