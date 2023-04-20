package com.example.kmatool

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.kmatool.utils.EVENTS_NOTIFY_CHANNEL
import com.example.kmatool.utils.EVENTS_NOTIFY_CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = EVENTS_NOTIFY_CHANNEL
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(EVENTS_NOTIFY_CHANNEL_ID, name, importance)
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}