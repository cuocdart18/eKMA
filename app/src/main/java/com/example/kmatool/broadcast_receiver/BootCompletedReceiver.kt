package com.example.kmatool.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kmatool.alarm.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.service.INoteService
import com.example.kmatool.data.models.service.IScheduleService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    private val TAG = BootCompletedReceiver::class.java.simpleName

    @Inject
    lateinit var scheduleService: IScheduleService

    @Inject
    lateinit var noteService: INoteService

    @Inject
    lateinit var dataLocalManager: IDataLocalManager

    @Inject
    lateinit var alarmEventsScheduler: AlarmEventsScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            resetAlarm()
        }
    }

    private fun resetAlarm() {
        CoroutineScope(Dispatchers.IO).launch {
            // Get local data here
            Data.getLocalData(noteService, scheduleService) {}
            // Set the alarm here
            val isNotify = dataLocalManager.getIsNotifyEvents()
            CoroutineScope(Dispatchers.IO).launch {
                if (isNotify) {
                    // get value of Map, set alarm it
                    launch { Data.periodsDayMap.forEach { alarmEventsScheduler.scheduleEvents(it.value) } }
                    launch { Data.notesDayMap.forEach { alarmEventsScheduler.scheduleEvents(it.value) } }
                } else {
                    // get value of Map, cancel alarm it
                    launch { Data.periodsDayMap.forEach { alarmEventsScheduler.cancelEvents(it.value) } }
                    launch { Data.notesDayMap.forEach { alarmEventsScheduler.cancelEvents(it.value) } }
                }
            }
        }
    }
}