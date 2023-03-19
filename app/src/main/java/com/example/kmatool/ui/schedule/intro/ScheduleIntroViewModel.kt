package com.example.kmatool.ui.schedule.intro

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmatool.data.repositories.DataStoreManager
import kotlinx.coroutines.*

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