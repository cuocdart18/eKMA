package com.app.ekma.activities.calling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.PENDING_INVITE_TIME
import com.app.ekma.common.PUBLISHER_ROLE
import com.app.ekma.common.CountDownTimer
import com.app.ekma.common.DEFAULT_UID
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.RTC_TOKEN_TYPE
import com.app.ekma.common.Resource
import com.app.ekma.common.TOKEN_EXPIRED_TIME
import com.app.ekma.common.removeStudentCode
import com.app.ekma.data.models.AgoraTokenRequest
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.service.IAgoraService
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.firebase.KEY_ROOMS_COLL
import com.app.ekma.firebase.KEY_ROOM_MEMBERS
import com.app.ekma.firebase.MSG_CANCEL
import com.app.ekma.firebase.MSG_INVITE
import com.app.ekma.firebase.MSG_INVITER_CODE
import com.app.ekma.firebase.MSG_OPERATION
import com.app.ekma.firebase.MSG_SEND_CHANNEL_TOKEN
import com.app.ekma.firebase.MSG_TYPE
import com.app.ekma.firebase.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _isExpiredActivation = MutableLiveData<Boolean>()
    val isExpiredActivation: LiveData<Boolean> = _isExpiredActivation
    private val timer = CountDownTimer(PENDING_INVITE_TIME) {
        _isExpiredActivation.value = true
    }

    init {
        _isExpiredActivation.value = false
        timer.start()
    }

    fun getReceivers(
        callback: (String) -> Unit
    ) {
        viewModelScope.launch {
            myStudentCode = ProfileSingleton().studentCode
            val roomMembers = firestore.collection(KEY_ROOMS_COLL)
                .document(roomId)
                .get()
                .await()
                .get(KEY_ROOM_MEMBERS) as List<String>
            receiverCodes = removeStudentCode(roomMembers, myStudentCode)
            callback(receiverCodes.toString())
        }
    }

    fun inviteReceiver() {
        viewModelScope.launch {
            // get regisToken and send message invitation
            receiverCodes.forEach { code ->
                launch {
                    sendMessageInvitation(code, MSG_INVITE)
                }
            }
        }
    }

    fun cancelInvitation(callback: () -> Unit) {
        viewModelScope.launch {
            // get regisToken and send message invitation
            receiverCodes.forEach { code ->
                sendMessageInvitation(code, MSG_CANCEL)
            }
            BusyCalling.setData(false)
            callback()
        }
    }

    private suspend fun sendMessageInvitation(
        code: String,
        operation: String
    ) {
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