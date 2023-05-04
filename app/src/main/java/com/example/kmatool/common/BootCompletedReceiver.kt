package com.example.kmatool.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.kmatool.data.repositories.ScheduleRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    private val TAG = BootCompletedReceiver::class.java.simpleName

    @Inject
    lateinit var scheduleRepository: ScheduleRepository

    @Inject
    lateinit var dataLocalManager: DataLocalManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            Toast.makeText(context, "boot completed", Toast.LENGTH_SHORT).show()
            resetAlarm(context)
        }
    }

    private fun resetAlarm(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            // Get local data here
            scheduleRepository.getLocalData()
            // Set the alarm here
            dataLocalManager.getIsNotifyEvents { state ->
                CoroutineScope(Dispatchers.Default).launch {
                    val alarmScheduler = AlarmEventsScheduler(context)
                    if (state) {
                        // get value of Map, set alarm it
                        launch { Data.periodsDayMap.forEach { alarmScheduler.scheduleEvents(it.value) } }
                        launch { Data.notesDayMap.forEach { alarmScheduler.scheduleEvents(it.value) } }
                    } else {
                        // get value of Map, cancel alarm it
                        launch { Data.periodsDayMap.forEach { alarmScheduler.cancelEvents(it.value) } }
                        launch { Data.notesDayMap.forEach { alarmScheduler.cancelEvents(it.value) } }
                    }
                }
                Log.d(TAG, "listen notify event state=$state")
                Log.i(TAG, "resetAlarm: successfully")
                cancel()
            }
        }
    }
}