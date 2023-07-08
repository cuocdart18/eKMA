package com.example.kmatool.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.kmatool.broadcast_receiver.AlarmReceiver
import com.example.kmatool.common.Data
import com.example.kmatool.common.KEY_EVENT
import com.example.kmatool.data.models.Event

class AlarmEventsScheduler(private val context: Context) : AlarmScheduler {
    private val TAG = AlarmEventsScheduler::class.java.simpleName
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleEvent(event: Event) {
        val dateTimeLong = event.getTimeMillis()
        if (dateTimeLong < System.currentTimeMillis()) {
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(KEY_EVENT, event)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.getEventId(),
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

        val intent = Intent(context, AlarmReceiver::class.java).apply {
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.getEventId(),
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
        schedulePeriods()
        scheduleNotes()
    }

    private fun schedulePeriods() {
        Data.periodsDayMap.forEach { scheduleEvents(it.value) }
    }

    private fun scheduleNotes() {
        Data.notesDayMap.forEach { scheduleEvents(it.value) }
    }

    fun clearAlarmEvents() {
        cancelPeriods()
        cancelNotes()
    }

    fun cancelPeriods() {
        Data.periodsDayMap.forEach { cancelEvents(it.value) }
    }

    private fun cancelNotes() {
        Data.notesDayMap.forEach { cancelEvents(it.value) }
    }
}