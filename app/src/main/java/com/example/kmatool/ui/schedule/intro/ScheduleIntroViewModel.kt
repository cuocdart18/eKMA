package com.example.kmatool.ui.schedule.intro

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class ScheduleIntroViewModel @Inject constructor() : BaseViewModel() {
    override val TAG = ScheduleIntroViewModel::class.java.simpleName

    fun getLoginState(
        context: Context,
        callback: (res: Boolean) -> Unit
    ) {
        logDebug("getLoginState")
        viewModelScope.launch(Dispatchers.IO) {
            /*val dataStoreManager = DataStoreManager(context)
            dataStoreManager.isLoginDataStoreFlow.collect {
                withContext(Dispatchers.Main) {
                    callback(it)
                }
                cancel()
            }*/
        }
    }
}