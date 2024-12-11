package com.app.ekma.firebase

import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.WorkManager
import com.app.ekma.common.CALL_WORKER_TAG
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.INCOMING_CALL_ID
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.hideNotification
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.work.WorkRunner
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = MyFirebaseMessagingService::class.java.simpleName

    @Inject
    lateinit var fcmService: IFcmService

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.i(TAG, "onMessageReceived: ${message.data}")
        val operation = message.data[MSG_OPERATION].toString()

        if (operation.isEmpty()) {
            return
        }
        when (operation) {
            MSG_INVITE -> {
                val inviterCode = message.data[MSG_INVITER_CODE].toString()
                val type = message.data[MSG_TYPE].toString()
                if (BusyCalling()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val senderToken = fcmService.getFcmToken(inviterCode)
                        // send data
                        val data = mapOf(
                            MSG_OPERATION to MSG_REJECT
                        )
                        val fcmDataMessage = FcmDataMessage(senderToken, data)
                        fcmService.sendCallInvitationMessage(fcmDataMessage)
                    }
                } else {
                    WorkRunner.runIncomingCallWorker(
                        workManager = WorkManager.getInstance(applicationContext),
                        inviterCode = inviterCode,
                        type = type
                    )
                }
            }

            MSG_ACCEPT -> {
                val intent = Intent(MSG_OPERATION)
                val bundle = bundleOf(
                    MSG_OPERATION to MSG_ACCEPT
                )
                intent.putExtras(bundle)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }

            MSG_SEND_CHANNEL_TOKEN -> {
                val intent = Intent(MSG_OPERATION)
                val token = message.data[CHANNEL_TOKEN].toString()
                val roomId = message.data[KEY_PASS_CHAT_ROOM_ID].toString()
                val bundle = bundleOf(
                    MSG_OPERATION to MSG_SEND_CHANNEL_TOKEN,
                    CHANNEL_TOKEN to token,
                    KEY_PASS_CHAT_ROOM_ID to roomId
                )
                intent.putExtras(bundle)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }

            MSG_REJECT -> {
                val intent = Intent(MSG_OPERATION)
                val bundle = bundleOf(
                    MSG_OPERATION to MSG_REJECT
                )
                intent.putExtras(bundle)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }

            MSG_CANCEL -> {
                hideNotification(applicationContext, INCOMING_CALL_ID)
                WorkManager.getInstance(applicationContext).cancelAllWorkByTag(CALL_WORKER_TAG)
                val intent = Intent(MSG_OPERATION)
                val bundle = bundleOf(
                    MSG_OPERATION to MSG_CANCEL
                )
                intent.putExtras(bundle)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                BusyCalling.setData(false)
            }
        }
    }
}