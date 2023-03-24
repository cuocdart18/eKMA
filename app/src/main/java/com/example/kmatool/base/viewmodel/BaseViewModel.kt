package com.example.kmatool.base.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    protected open val TAG = ""

    // LIFECYCLE
    override fun onCleared() {
        super.onCleared()
        logLifecycle("onCleared")
    }

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