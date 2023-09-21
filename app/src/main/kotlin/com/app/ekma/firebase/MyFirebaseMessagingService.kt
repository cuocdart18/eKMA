package com.app.ekma.firebase

import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ekma.activities.IncomingInvitationActivity
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = MyFirebaseMessagingService::class.java.simpleName

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.i(TAG, "onMessageReceived: ${message.data}")
        val operation = message.data[MSG_OPERATION].toString()

        if (operation.isNotEmpty()) {
            when (operation) {
                MSG_INVITE -> {
                    val inviterCode = message.data[MSG_INVITER_CODE].toString()
                    val type = message.data[MSG_TYPE].toString()
                    val intent = Intent(applicationContext, IncomingInvitationActivity::class.java)
                    val bundle = bundleOf(
                        MSG_INVITER_CODE to inviterCode,
                        MSG_TYPE to type
                    )
                    intent.putExtras(bundle)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
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
                    val intent = Intent(MSG_OPERATION)
                    val bundle = bundleOf(
                        MSG_OPERATION to MSG_CANCEL
                    )
                    intent.putExtras(bundle)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
            }
        }
    }
}