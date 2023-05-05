package com.example.kmatool.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.kmatool.data.models.Event

class AlarmEventsScheduler(private val context: Context) : AlarmScheduler {
    private val TAG = AlarmEventsScheduler::class.java.simpleName
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleEvent(event: Event) {
        val dateTimeLong = event.getTimeMillis()
        if (dateTimeLong < System.currentTimeMillis()) {
            return
        }

        Log.d(TAG, "schedule Event: ${event.getContentTitleNotify()}")
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(KEY_EVENT, event)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.hashCode(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dateTimeLong,
            pendingIntent
        )
    }

    override fun cancelEvent(event: Event) {
        val dateTimeLong = event.getTimeMillis()
        if (dateTimeLong < System.currentTimeMillis()) {
            return
        }

        Log.d(TAG, "cancel Event: ${event.getContentTitleNotify()}")
        val intent = Intent(context, AlarmReceiver::class.java).apply {
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.hashCode(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleEvents(events: List<Event>) {
        events.forEach { scheduleEvent(it) }
    }

    fun cancelEvents(events: List<Event>) {
        events.forEach { cancelEvent(it) }
    }

    fun scheduleAlarmEvents() {
        Data.periodsDayMap.forEach { scheduleEvents(it.value) }
        Data.notesDayMap.forEach { scheduleEvents(it.value) }
    }

    fun clearAlarmEvents() {
        Data.periodsDayMap.forEach { cancelEvents(it.value) }
        Data.notesDayMap.forEach { cancelEvents(it.value) }
    }
}