package com.app.ekma.broadcast_receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.app.ekma.R
import com.app.ekma.activities.MainActivity
import com.app.ekma.common.EVENTS_NOTIFY_CHANNEL_ID
import com.app.ekma.common.KEY_EVENT
import com.app.ekma.common.KEY_PASS_NOTE_OBJ
import com.app.ekma.common.NOTE_TYPE
import com.app.ekma.data.models.Event
import com.app.ekma.data.models.Note

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        val event = intent.getSerializableExtra(KEY_EVENT) as Event
        showNotification(context, event)
    }

    private fun showNotification(context: Context, event: Event) {
        val eventBuilder = NotificationCompat.Builder(context, EVENTS_NOTIFY_CHANNEL_ID)
            .setSmallIcon(event.getSmallIconNotify())
            .setContentTitle(event.getContentTitleNotify())
            .setSubText(event.getSubTextNotify())
            .setContentText(event.getContentTextNotify())
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(event.getContentBigTextNotify())
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        if (event.type == NOTE_TYPE) {
            val note = event as Note
            val noteBundle = bundleOf(
                KEY_PASS_NOTE_OBJ to note
            )
            val pendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_app_global_graph)
                .setDestination(R.id.noteDetailFragment)
                .setArguments(noteBundle)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()
            eventBuilder.setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }

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