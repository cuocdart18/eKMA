package com.example.kmatool.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.kmatool.data.models.Event
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.Period
import com.example.kmatool.utils.NOTE_TYPE
import com.example.kmatool.utils.PERIOD_TYPE

class AlarmEventsScheduler(private val context: Context) : AlarmScheduler {

    @RequiresApi(Build.VERSION_CODES.M)
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun scheduleEvents(event: Event) {
        val dateTimeLong = event.getTimeMillis()
        if (dateTimeLong < System.currentTimeMillis()) {
            Log.i("AlarmEventsScheduler", "closed ${event.getDateTime()}")
            return
        }
        Log.i("AlarmEventsScheduler", "set for alarm ${event.getDateTime()}")

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            // put something
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun cancel(event: Event) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            // put something
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.hashCode(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}