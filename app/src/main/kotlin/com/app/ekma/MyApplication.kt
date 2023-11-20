package com.app.ekma

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.app.ekma.common.EVENTS_NOTIFY_CHANNEL
import com.app.ekma.common.EVENTS_NOTIFY_CHANNEL_ID
import com.app.ekma.common.GET_SCHE_CHANNEL
import com.app.ekma.common.GET_SCHE_CHANNEL_ID
import com.app.ekma.common.UPDATE_SCHE_CHANNEL
import com.app.ekma.common.UPDATE_SCHE_CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel for notify events
            val eventNotifyName = EVENTS_NOTIFY_CHANNEL
            val eventNotifyImportance = NotificationManager.IMPORTANCE_HIGH
            val eventNotifyChannel =
                NotificationChannel(
                    EVENTS_NOTIFY_CHANNEL_ID,
                    eventNotifyName,
                    eventNotifyImportance
                )
            // Create the NotificationChannel for update schedule
            val updateScheName = UPDATE_SCHE_CHANNEL
            val updateScheImportance = NotificationManager.IMPORTANCE_HIGH
            val updateScheChannel =
                NotificationChannel(
                    UPDATE_SCHE_CHANNEL_ID,
                    updateScheName,
                    updateScheImportance
                )
            // Create the NotificationChannel for get schedule
            val getScheName = GET_SCHE_CHANNEL
            val getScheImportance = NotificationManager.IMPORTANCE_HIGH
            val getScheChannel =
                NotificationChannel(
                    GET_SCHE_CHANNEL_ID,
                    getScheName,
                    getScheImportance
                )

            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(
                listOf(
                    eventNotifyChannel,
                    updateScheChannel,
                    getScheChannel
                )
            )
        }
    }
}