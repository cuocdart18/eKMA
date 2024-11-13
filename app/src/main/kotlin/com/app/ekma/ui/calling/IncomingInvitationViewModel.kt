package com.app.ekma.ui.calling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.CountDownTimer
import com.app.ekma.common.PENDING_INVITE_TIME
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.service.IFcmService
import com.app.ekma.firebase.MSG_OPERATION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomingInvitationViewModel @Inject constructor(
    private val fcmService: IFcmService
) : BaseViewModel() {
    override val TAG = IncomingInvitationViewModel::class.java.simpleName

    lateinit var inviterCode: String
    lateinit var callType: String

    private val _isExpiredActivation = MutableLiveData<Boolean>()
    val isExpiredActivation: LiveData<Boolean> = _isExpiredActivation
    private val timer = CountDownTimer(PENDING_INVITE_TIME) {
        _isExpiredActivation.value = true
    }

    init {
        _isExpiredActivation.value = false
    }

    fun sendMessageInvitationResponse(
        type: String,
        callback: () -> Unit
    ) {
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