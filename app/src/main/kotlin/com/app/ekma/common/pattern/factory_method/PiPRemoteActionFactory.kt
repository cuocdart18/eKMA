package com.app.ekma.common.pattern.factory_method

import android.app.PendingIntent
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import com.app.ekma.R
import com.app.ekma.broadcast_receiver.MyCallingReceiver
import com.app.ekma.common.CALLING_OPERATION
import com.app.ekma.common.EARPIECE_AUDIO_ROUTE
import com.app.ekma.common.EARPIECE_AUDIO_ROUTE_REQUEST_CODE
import com.app.ekma.common.HANG_UP
import com.app.ekma.common.HANG_UP_REQUEST_CODE
import com.app.ekma.common.LEAVE_ROOM
import com.app.ekma.common.LEAVE_ROOM_REQUEST_CODE
import com.app.ekma.common.MUTE_CAMERA
import com.app.ekma.common.MUTE_CAMERA_REQUEST_CODE
import com.app.ekma.common.MUTE_MIC
import com.app.ekma.common.MUTE_MIC_REQUEST_CODE
import com.app.ekma.common.SPEAKER_AUDIO_ROUTE
import com.app.ekma.common.SPEAKER_AUDIO_ROUTE_REQUEST_CODE
import com.app.ekma.common.UNMUTE_CAMERA
import com.app.ekma.common.UNMUTE_CAMERA_REQUEST_CODE
import com.app.ekma.common.UNMUTE_MIC
import com.app.ekma.common.UNMUTE_MIC_REQUEST_CODE

enum class RemoteActionType {
    MUTE_MIC,
    UNMUTE_MIC,
    MUTE_CAMERA,
    UNMUTE_CAMERA,
    EARPIECE_AUDIO_ROUTE,
    SPEAKER_AUDIO_ROUTE,
    LEAVE_ROOM,
    HANG_UP
}

class PiPRemoteActionFactory {

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun create(context: Context, type: RemoteActionType): RemoteAction = when (type) {
            RemoteActionType.MUTE_MIC -> {
                val muteMicIntent =
                    Intent(context, MyCallingReceiver::class.java).apply {
                        putExtra(CALLING_OPERATION, MUTE_MIC)
                    }
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.mic_off_outline_white_24dp),
                    "Mute mic",
                    "Mute mic",
                    PendingIntent.getBroadcast(
                        context,
                        MUTE_MIC_REQUEST_CODE,
                        muteMicIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            RemoteActionType.UNMUTE_MIC -> {
                val unmuteMicIntent =
                    Intent(context, MyCallingReceiver::class.java).apply {
                        putExtra(CALLING_OPERATION, UNMUTE_MIC)
                    }
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.mic_outline_white_24dp),
                    "Unmute mic",
                    "Unmute mic",
                    PendingIntent.getBroadcast(
                        context,
                        UNMUTE_MIC_REQUEST_CODE,
                        unmuteMicIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            RemoteActionType.MUTE_CAMERA -> {
                val muteCameraIntent =
                    Intent(context, MyCallingReceiver::class.java).apply {
                        putExtra(CALLING_OPERATION, MUTE_CAMERA)
                    }
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.videocam_off_outline_white_24dp),
                    "Mute camera",
                    "Mute camera",
                    PendingIntent.getBroadcast(
                        context,
                        MUTE_CAMERA_REQUEST_CODE,
                        muteCameraIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            RemoteActionType.UNMUTE_CAMERA -> {
                val unmuteCameraIntent =
                    Intent(context, MyCallingReceiver::class.java).apply {
                        putExtra(CALLING_OPERATION, UNMUTE_CAMERA)
                    }
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.videocam_outline_white_24dp),
                    "Unmute camera",
                    "Unmute camera",
                    PendingIntent.getBroadcast(
                        context,
                        UNMUTE_CAMERA_REQUEST_CODE,
                        unmuteCameraIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            RemoteActionType.EARPIECE_AUDIO_ROUTE -> {
                val earpieceAudioRouteIntent =
                    Intent(context, MyCallingReceiver::class.java).apply {
                        putExtra(CALLING_OPERATION, EARPIECE_AUDIO_ROUTE)
                    }
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.volume_low_outline_white_24dp),
                    "Earpiece audio route",
                    "Earpiece audio route",
                    PendingIntent.getBroadcast(
                        context,
                        EARPIECE_AUDIO_ROUTE_REQUEST_CODE,
                        earpieceAudioRouteIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            RemoteActionType.SPEAKER_AUDIO_ROUTE -> {
                val speakerAudioRouteIntent =
                    Intent(context, MyCallingReceiver::class.java).apply {
                        putExtra(CALLING_OPERATION, SPEAKER_AUDIO_ROUTE)
                    }
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.volume_high_outline_white_24dp),
                    "Speaker audio route",
                    "Speaker audio route",
                    PendingIntent.getBroadcast(
                        context,
                        SPEAKER_AUDIO_ROUTE_REQUEST_CODE,
                        speakerAudioRouteIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            RemoteActionType.LEAVE_ROOM -> {
                val leaveRoomIntent =
                    Intent(context, MyCallingReceiver::class.java).apply {
                        putExtra(CALLING_OPERATION, LEAVE_ROOM)
                    }
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.call_outline_white_24dp),
                    "Leave room",
                    "Leave room",
                    PendingIntent.getBroadcast(
                        context,
                        LEAVE_ROOM_REQUEST_CODE,
                        leaveRoomIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            RemoteActionType.HANG_UP -> {
                val hangUpIntent =
                    Intent(context, MyCallingReceiver::class.java).apply {
                        putExtra(CALLING_OPERATION, HANG_UP)
                    }
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.call_outline_white_24dp),
                    "Hang up",
                    "Hang up",
                    PendingIntent.getBroadcast(
                        context,
                        HANG_UP_REQUEST_CODE,
                        hangUpIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        }
    }
}