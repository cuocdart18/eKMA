package com.example.kmatool.activities

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.common.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    // this variable is used to check the first collect, if == 0 then don't anything
    private var collectNotifyStateCounter = 0

    fun listenAlarmLocalEventsState(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.isNotifyEventsDataStoreFlow.collect { state ->
                if (collectNotifyStateCounter == 0) {
                    collectNotifyStateCounter = 1
                } else {
                    viewModelScope.launch(Dispatchers.Default) {
                        val alarmScheduler = AlarmEventsScheduler(context)
                        if (state) {
                            // get value of Map, set alarm it
                            launch { Data.periodsDayMap.forEach { alarmScheduler.scheduleEvents(it.value) } }
                            launch { Data.notesDayMap.forEach { alarmScheduler.scheduleEvents(it.value) } }
                            logDebug("set notify event state=$state")
                        } else {
                            // get value of Map, cancel alarm it
                            launch { Data.periodsDayMap.forEach { alarmScheduler.cancelEvents(it.value) } }
                            launch { Data.notesDayMap.forEach { alarmScheduler.cancelEvents(it.value) } }
                            logDebug("cancel notify event state=$state")
                        }
                    }
                }
            }
        }
    }
}