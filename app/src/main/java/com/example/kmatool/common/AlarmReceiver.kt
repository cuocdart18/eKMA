package com.example.kmatool.common

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kmatool.R
import com.example.kmatool.utils.EVENTS_NOTIFY_CHANNEL_ID
import com.example.kmatool.utils.EVENTS_NOTIFY_ID

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: test thong bao")

        val builder = NotificationCompat.Builder(context!!, EVENTS_NOTIFY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_person_24)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line...")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(EVENTS_NOTIFY_ID, builder.build())
        }
    }
}