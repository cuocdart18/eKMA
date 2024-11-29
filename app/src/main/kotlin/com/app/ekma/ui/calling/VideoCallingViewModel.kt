package com.app.ekma.ui.calling

import android.app.RemoteAction
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.CallTimer
import com.app.ekma.common.formatCallingTimer
import com.app.ekma.common.pattern.factory_method.PiPRemoteActionFactory
import com.app.ekma.common.pattern.factory_method.RemoteActionType
import com.app.ekma.firebase.AVATAR_FILE
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.KEY_USER_NAME
import com.app.ekma.firebase.USERS_DIR
import com.app.ekma.firebase.firestore
import com.app.ekma.firebase.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoCallingViewModel @Inject constructor(

) : BaseViewModel() {
    override val TAG = VideoCallingViewModel::class.java.simpleName

    var token = ""
    var roomId = ""
    var friendCode = ""

    private val _callTimer = MutableStateFlow("00:00")
    val callTimer: StateFlow<String>
        get() = _callTimer.asStateFlow()

    private val timer = CallTimer(
        millisStartPoint = 0L,
        countInterval = 1000L,
        runAtStart = false,
        onEnd = {
            val totalTime = it
            logInfo("totalTime = $totalTime")
        },
        onTick = {
            _callTimer.value = formatCallingTimer(it)
        }
    )

    fun startCallTimer() {
        timer.start()
    }

    fun stopCallTimer() {
        timer.stop()
    }

    private val _imageAvatarUri = MutableStateFlow<Uri?>(null)
    val imageAvatarUri: StateFlow<Uri?>
        get() = _imageAvatarUri.asStateFlow()

    private val _friendName = MutableStateFlow("")
    val friendName: StateFlow<String>
        get() = _friendName.asStateFlow()

    private val _isMuteMic = MutableStateFlow(false)
    val isMuteMic: StateFlow<Boolean> = _isMuteMic.asStateFlow()

    private val _isMuteCamera = MutableStateFlow(false)
    val isMuteCamera: StateFlow<Boolean> = _isMuteCamera.asStateFlow()

    private val _isCameraFront = MutableStateFlow(true)
    val isCameraFront: StateFlow<Boolean> = _isCameraFront.asStateFlow()

    fun toggleMicState() {
        viewModelScope.launch {
            _isMuteMic.value = !_isMuteMic.value
        }
    }

    fun toggleCameraState() {
        viewModelScope.launch {
            _isMuteCamera.value = !_isMuteCamera.value
        }
    }

    fun toggleSwitchCamera(){
        viewModelScope.launch {
            _isCameraFront.value = !_isCameraFront.value
        }
    }

    fun getSenderInformation() {
        viewModelScope.launch {
            // get avatar friend
            storage.child("$USERS_DIR/$friendCode/$AVATAR_FILE")
                .downloadUrl
                .addOnSuccessListener {
                    _imageAvatarUri.value = it
                }

            // get friend name
            firestore.collection(KEY_USERS_COLL)
                .document(friendCode)
                .get()
                .addOnSuccessListener { snap ->
                    _friendName.value = snap.getString(KEY_USER_NAME) ?: "Friend"
                }
        }
    }

    fun getPiPRemoteActions(context: Context): List<RemoteAction> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val micAction = if (_isMuteMic.value) {
                PiPRemoteActionFactory.create(context, RemoteActionType.MUTE_MIC)
            } else {
                PiPRemoteActionFactory.create(context, RemoteActionType.UNMUTE_MIC)
            }
            val cameraAction = if (_isMuteCamera.value) {
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

    fun sendCallTypeMessage() {

    }
}