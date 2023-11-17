package com.app.ekma.activities

import android.app.RemoteAction
import android.content.Context
import android.os.Build
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.pattern.factory_method.PiPRemoteActionFactory
import com.app.ekma.common.pattern.factory_method.RemoteActionType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoCallingViewModel @Inject constructor(

) : BaseViewModel() {
    override val TAG = VideoCallingViewModel::class.java.simpleName

    var token = ""
    var roomId = ""
    var isMuteMic = true
    var isMuteCamera = true

    fun getPiPRemoteActions(context: Context): List<RemoteAction> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val micAction = if (isMuteMic) {
                PiPRemoteActionFactory.create(context, RemoteActionType.MUTE_MIC)
            } else {
                PiPRemoteActionFactory.create(context, RemoteActionType.UNMUTE_MIC)
            }
            val cameraAction = if (isMuteCamera) {
                PiPRemoteActionFactory.create(context, RemoteActionType.MUTE_CAMERA)
            } else {
                PiPRemoteActionFactory.create(context, RemoteActionType.UNMUTE_CAMERA)
            }
            val leaveRoomAction =
                PiPRemoteActionFactory.create(context, RemoteActionType.LEAVE_ROOM)
            listOf(micAction, cameraAction, leaveRoomAction)
        } else {
            emptyList()
        }
    }
}