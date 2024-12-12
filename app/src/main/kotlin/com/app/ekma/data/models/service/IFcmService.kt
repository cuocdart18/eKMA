package com.app.ekma.data.models.service

import com.app.ekma.data.models.FcmDataMessage

interface IFcmService {

    suspend fun getFcmToken(code: String): String

    suspend fun sendCallInvitationMessage(
        fcmDataMessage: FcmDataMessage
    )

    suspend fun sendNewMessage(
        fcmDataMessage: FcmDataMessage
    )
}