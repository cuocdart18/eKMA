package com.example.kmatool.broadcast_receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kmatool.common.EVENTS_NOTIFY_CHANNEL_ID
import com.example.kmatool.common.KEY_EVENT
import com.example.kmatool.data.models.Event

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        val event = intent?.getSerializableExtra(KEY_EVENT) as Event
        if (context != null) {
            showNotification(context, event)
        }
    }

    private fun showNotification(context: Context, event: Event) {
        val eventBuilder = NotificationCompat.Builder(context, EVENTS_NOTIFY_CHANNEL_ID)
            .setSmallIcon(event.getSmallIconNotify())
            /*.setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    event.getLargeIconNotify()
                )
            )*/
            .setContentTitle(event.getContentTitleNotify())
            .setSubText(event.getSubTextNotify())
            .setContentText(event.getContentTextNotify())
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(event.getContentBigTextNotify())
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
            notify(event.hashCode(), eventBuilder.build())
        }
    }
}