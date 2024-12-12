package com.app.ekma.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.ekma.R
import com.app.ekma.common.IMAGE_MSG
import com.app.ekma.common.INPUT_DATA_NEW_MSG_NOTIFICATION
import com.app.ekma.common.NEW_MSG_NOTIFY_CHANNEL_ID
import com.app.ekma.common.TEXT_MSG
import com.app.ekma.common.jsonStringToObject
import com.app.ekma.common.super_utils.bitmap.getBitmapFromUrl
import com.app.ekma.firebase.AVATAR_FILE
import com.app.ekma.firebase.KEY_MESSAGE_CONTENT_DOC
import com.app.ekma.firebase.KEY_MESSAGE_FROM_DOC
import com.app.ekma.firebase.KEY_MESSAGE_TYPE_DOC
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.KEY_USER_NAME
import com.app.ekma.firebase.USERS_DIR
import com.app.ekma.firebase.firestore
import com.app.ekma.firebase.storage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class ShowNewMessageNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = ShowNewMessageNotificationWorker::class.java.simpleName

    override suspend fun doWork(): Result {
        showNewMsgNotification()
        return Result.success()
    }

    private suspend fun showNewMsgNotification() {
        val dataMessage = inputData.getString(INPUT_DATA_NEW_MSG_NOTIFICATION).toString()

        val dataMap = jsonStringToObject<Map<String, Any>>(dataMessage)
        val content = dataMap[KEY_MESSAGE_CONTENT_DOC].toString()
        val from = dataMap[KEY_MESSAGE_FROM_DOC].toString()
        val type = dataMap[KEY_MESSAGE_TYPE_DOC].toString().toDouble().toInt()

        val fromName = firestore.collection(KEY_USERS_COLL)
            .document(from)
            .get()
            .await()
            .getString(KEY_USER_NAME) ?: "Friend"

        val imgUri = storage.child("$USERS_DIR/$from/$AVATAR_FILE")
            .downloadUrl
            .await()

        val subText = when (type) {
            TEXT_MSG -> "Đã gửi một tin nhắn"
            IMAGE_MSG -> "Đã gửi một ảnh"
            else -> "Đã gửi một tin nhắn"
        }

        val eventBuilder =
            NotificationCompat.Builder(applicationContext, NEW_MSG_NOTIFY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat)
                .setContentTitle(fromName)
                .setSubText(subText)
                .setContentText(content)
                .setLargeIcon(getBitmapFromUrl(applicationContext, imgUri))
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)

        val id = System.currentTimeMillis().toInt()
        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(id, eventBuilder.build())
        }
    }
}