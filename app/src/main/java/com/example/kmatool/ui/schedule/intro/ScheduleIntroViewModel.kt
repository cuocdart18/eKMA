package com.example.kmatool.ui.schedule.intro

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.DataStoreManager
import kotlinx.coroutines.*

class ScheduleIntroViewModel : BaseViewModel() {
    override val TAG = ScheduleIntroViewModel::class.java.simpleName

    fun getLoginState(
        context: Context,
        callback: (res: Boolean) -> Unit
    ) {
        logDebug("getLoginState")
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