package com.app.ekma.ui.calling

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.CountDownTimer
import com.app.ekma.common.DEFAULT_UID
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.PENDING_INVITE_TIME
import com.app.ekma.common.PUBLISHER_ROLE
import com.app.ekma.common.RTC_TOKEN_TYPE
import com.app.ekma.common.Resource
import com.app.ekma.common.TOKEN_EXPIRED_TIME
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.removeStudentCode
import com.app.ekma.data.models.AgoraTokenRequest
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.service.IAgoraService
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.firebase.AVATAR_FILE
import com.app.ekma.firebase.KEY_ROOMS_COLL
import com.app.ekma.firebase.KEY_ROOM_MEMBERS
import com.app.ekma.firebase.KEY_USERS_COLL
import com.app.ekma.firebase.KEY_USER_NAME
import com.app.ekma.firebase.MSG_CANCEL
import com.app.ekma.firebase.MSG_INVITE
import com.app.ekma.firebase.MSG_INVITER_CODE
import com.app.ekma.firebase.MSG_OPERATION
import com.app.ekma.firebase.MSG_SEND_CHANNEL_TOKEN
import com.app.ekma.firebase.MSG_TYPE
import com.app.ekma.firebase.USERS_DIR
import com.app.ekma.firebase.firestore
import com.app.ekma.firebase.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class OutgoingInvitationViewModel @Inject constructor(
    private val fcmService: IFcmService,
    private val agoraService: IAgoraService
) : BaseViewModel() {
    override val TAG = OutgoingInvitationViewModel::class.java.simpleName

    lateinit var roomId: String
    lateinit var callType: String
    private lateinit var receiverCodes: List<String>
    private lateinit var myStudentCode: String

    private val _imageAvatarUri = MutableStateFlow<Uri?>(null)
    val imageAvatarUri: StateFlow<Uri?>
        get() = _imageAvatarUri.asStateFlow()

    private val _friendName = MutableStateFlow("")
    val friendName: StateFlow<String>
        get() = _friendName.asStateFlow()

    private val _isExpiredActivation = MutableStateFlow(false)
    val isExpiredActivation: StateFlow<Boolean>
        get() = _isExpiredActivation.asStateFlow()
    private val timer = CountDownTimer(PENDING_INVITE_TIME) {
        _isExpiredActivation.value = true
    }

    private val _onCancelInvite = MutableSharedFlow<Boolean>()
    val onCancelInvite: SharedFlow<Boolean>
        get() = _onCancelInvite.asSharedFlow()

    init {
        timer.start()
    }

    fun getReceivers() {
        viewModelScope.launch {
            myStudentCode = ProfileSingleton().studentCode
            val roomMembers = firestore.collection(KEY_ROOMS_COLL)
                .document(roomId)
                .get()
                .await()
                .get(KEY_ROOM_MEMBERS) as List<String>
            receiverCodes = removeStudentCode(roomMembers, myStudentCode)

            // get avatar friend
            storage.child("$USERS_DIR/${receiverCodes.first()}/$AVATAR_FILE")
                .downloadUrl
                .addOnSuccessListener {
                    _imageAvatarUri.value = it
                }

            // get friend name
            firestore.collection(KEY_USERS_COLL)
                .document(receiverCodes.first())
                .get()
                .addOnSuccessListener { snap ->
                    _friendName.value = snap.getString(KEY_USER_NAME) ?: "Friend"
                }

            // get regisToken and send message invitation
            receiverCodes.forEach { code ->
                launch {
                    sendMessageInvitation(code, MSG_INVITE)
                }
            }
        }
    }

    fun cancelInvitation() {
        viewModelScope.launch {
            // get regisToken and send message invitation
            receiverCodes.forEach { code ->
                sendMessageInvitation(code, MSG_CANCEL)
            }
            BusyCalling.setData(false)
            _onCancelInvite.emit(true)
        }
    }

    private suspend fun sendMessageInvitation(code: String, operation: String) {
        val receiverToken = fcmService.getFcmToken(code)
        // send data
        val data = mapOf(
            MSG_INVITER_CODE to myStudentCode,
            MSG_OPERATION to operation,
            MSG_TYPE to callType
        )
        val fcmDataMessage = FcmDataMessage(receiverToken, data)
        fcmService.sendCallInvitationMessage(fcmDataMessage)
    }

    fun createChannelAndGetToken(
        callback: (String) -> Unit
    ) {
        timer.count = PENDING_INVITE_TIME
        viewModelScope.launch {
            val request = AgoraTokenRequest(
                tokenType = RTC_TOKEN_TYPE,
                channel = roomId,
                role = PUBLISHER_ROLE,
                uid = DEFAULT_UID,
                expired = TOKEN_EXPIRED_TIME
            )
            val tokenResource = agoraService.getToken(request)
            if (tokenResource is Resource.Success) {
                val token = tokenResource.data?.token ?: ""
                logError("token=$token")
                callback(token)
            } else if (tokenResource is Resource.Error) {
                logError("error=${tokenResource.message}")
                callback("")
            }
        }
    }

    fun sendChannelTokenToReceiver(
        token: String,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            receiverCodes.forEach { code ->
                val receiverToken = fcmService.getFcmToken(code)
                // send data
                val data = mapOf(
                    MSG_OPERATION to MSG_SEND_CHANNEL_TOKEN,
                    CHANNEL_TOKEN to token,
                    KEY_PASS_CHAT_ROOM_ID to roomId
                )
                val fcmDataMessage = FcmDataMessage(receiverToken, data)
                fcmService.sendCallInvitationMessage(fcmDataMessage)
            }
            callback()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timer.stop()
    }
}