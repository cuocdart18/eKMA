package com.app.ekma.ui.calling

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.CountDownTimer
import com.app.ekma.common.PENDING_INVITE_TIME
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.firebase.AVATAR_FILE
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.KEY_USER_NAME
import com.app.ekma.firebase.MSG_AUDIO_CALL_TYPE
import com.app.ekma.firebase.MSG_OPERATION
import com.app.ekma.firebase.MSG_VIDEO_CALL_TYPE
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
class IncomingInvitationViewModel @Inject constructor(
    private val fcmService: IFcmService
) : BaseViewModel() {
    override val TAG = IncomingInvitationViewModel::class.java.simpleName

    lateinit var inviterCode: String
    lateinit var callType: String

    private val _imageAvatarUri = MutableStateFlow<Uri?>(null)
    val imageAvatarUri: StateFlow<Uri?>
        get() = _imageAvatarUri.asStateFlow()

    private val _friendName = MutableStateFlow("")
    val friendName: StateFlow<String>
        get() = _friendName.asStateFlow()

    private val _callTypeName = MutableStateFlow("")
    val callTypeName: StateFlow<String>
        get() = _callTypeName.asStateFlow()

    private val _isExpiredActivation = MutableStateFlow(false)
    val isExpiredActivation: StateFlow<Boolean>
        get() = _isExpiredActivation.asStateFlow()
    private val timer = CountDownTimer(PENDING_INVITE_TIME) {
        _isExpiredActivation.value = true
    }

    fun setCallTypeName(type: String) {
        viewModelScope.launch {
            when (type) {
                MSG_AUDIO_CALL_TYPE -> {
                    _callTypeName.value = "Audio"
                }

                MSG_VIDEO_CALL_TYPE -> {
                    _callTypeName.value = "Video"
                }
            }
        }
    }

    fun getSenderInformation() {
        viewModelScope.launch {
            // get avatar friend
            storage.child("$USERS_DIR/$inviterCode/$AVATAR_FILE")
                .downloadUrl
                .addOnSuccessListener {
                    _imageAvatarUri.value = it
                }

            // get friend name
            firestore.collection(KEY_USERS_COLL)
                .document(inviterCode)
                .get()
                .addOnSuccessListener { snap ->
                    _friendName.value = snap.getString(KEY_USER_NAME) ?: "Friend"
                }
        }
    }

    fun sendMessageInvitationResponse(type: String, callback: () -> Unit) {
        viewModelScope.launch {
            val senderToken = fcmService.getFcmToken(inviterCode)
            // send data
            val data = mapOf(
                MSG_OPERATION to type
            )
            val fcmDataMessage = FcmDataMessage(senderToken, data)
            fcmService.sendCallInvitationMessage(fcmDataMessage)
            callback()
        }
    }

    fun channelTokenPending() {
        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer.stop()
    }
}