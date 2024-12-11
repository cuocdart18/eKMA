package com.app.ekma.work

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.app.ekma.R
import com.app.ekma.broadcast_receiver.DeclineCallReceiver
import com.app.ekma.common.INCOMING_CALL_ID
import com.app.ekma.common.INCOMING_CALL_NOTIFY_CHANNEL_ID
import com.app.ekma.common.INPUT_DATA_CALL_TYPE
import com.app.ekma.common.INPUT_DATA_INVITER_CODE
import com.app.ekma.common.KEY_PASS_AUTO_JOIN_CALL
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.pattern.singleton.IsAppRunning
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.KEY_USER_NAME
import com.app.ekma.firebase.MSG_AUDIO_CALL_TYPE
import com.app.ekma.firebase.MSG_INVITER_CODE
import com.app.ekma.firebase.MSG_TYPE
import com.app.ekma.firebase.MSG_VIDEO_CALL_TYPE
import com.app.ekma.firebase.firestore
import com.app.ekma.ui.calling.IncomingInvitationActivity
import com.app.ekma.ui.splash.SplashActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class IncomingCallWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = IncomingCallWorker::class.java.simpleName

    override suspend fun doWork(): Result {
        BusyCalling.setData(true)
        val inviterCode = inputData.getString(INPUT_DATA_INVITER_CODE).toString()
        val type = inputData.getString(INPUT_DATA_CALL_TYPE).toString()
        showNotification(applicationContext, inviterCode, type)
        WorkRunner.runHideIncomingNotificationWorker(WorkManager.getInstance(applicationContext))
        return Result.success()
    }

    private suspend fun showNotification(
        context: Context,
        inviterCode: String,
        type: String
    ) {
        val nameNoti = firestore.collection(KEY_USERS_COLL)
            .document(inviterCode)
            .get()
            .await().getString(KEY_USER_NAME) ?: "Friend"
        val typeNoti = when (type) {
            MSG_AUDIO_CALL_TYPE -> "Audio"
            MSG_VIDEO_CALL_TYPE -> "Video"
            else -> "Unknown"
        }

        val declinePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, DeclineCallReceiver::class.java).apply {
                putExtras(
                    bundleOf(
                        MSG_INVITER_CODE to inviterCode,
                        MSG_TYPE to type
                    )
                )
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val answerPendingIntent = PendingIntent.getActivity(
            context,
            1,
            if (IsAppRunning()) {
                Intent(context, IncomingInvitationActivity::class.java).apply {
                    putExtras(
                        bundleOf(
                            KEY_PASS_AUTO_JOIN_CALL to true,
                            MSG_INVITER_CODE to inviterCode,
                            MSG_TYPE to type
                        )
                    )
                }
            } else {
                Intent(context, SplashActivity::class.java).apply {
                    putExtras(
                        bundleOf(
                            MSG_INVITER_CODE to inviterCode,
                            MSG_TYPE to type
                        )
                    )
                }
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val fullscreenPendingIntent = PendingIntent.getActivity(
            context,
            2,
            Intent(context, IncomingInvitationActivity::class.java).apply {
                putExtras(
                    bundleOf(
                        MSG_INVITER_CODE to inviterCode,
                        MSG_TYPE to type
                    )
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val callBuilder = NotificationCompat.Builder(context, INCOMING_CALL_NOTIFY_CHANNEL_ID)
            .setSmallIcon(R.drawable.school_outline_black_24dp)
            .setContentTitle(nameNoti)
            .setContentText("$typeNoti call")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE) // Rung mặc định
            .setVibrate(longArrayOf(0, 500, 500, 500)) // Custom vibration pattern
            .addAction(R.drawable.ic_phone_reject, "DECLINE", declinePendingIntent)
            .addAction(R.drawable.ic_phone_accept, "ANSWER", answerPendingIntent)
            .setFullScreenIntent(fullscreenPendingIntent, true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(INCOMING_CALL_ID, callBuilder.build())
        }
    }
}