package com.example.kmatool.view_model.schedule

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmatool.local.DataStoreManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class ScheduleIntroViewModel : ViewModel() {
    private val TAG = ScheduleIntroViewModel::class.java.simpleName

    fun getLoginState(
        context: Context,
        callback: (res: Boolean) -> Unit
    ) {
        Log.d(TAG, "get login state")
        viewModelScope.launch(Dispatchers.IO) {
            val dataStoreManager = DataStoreManager(context)
            dataStoreManager.isLoginDataStoreFlow.collect {
                withContext(Dispatchers.Main) {
                    callback(it)
                }
                cancel()
            }
        }
    }
}