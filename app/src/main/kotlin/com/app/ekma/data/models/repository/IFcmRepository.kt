package com.app.ekma.data.models.repository

import com.app.ekma.data.models.FcmDataMessage

interface IFcmRepository {

    suspend fun sendCallInvitationMessage(
        fcmDataMessage: FcmDataMessage
    )

    suspend fun sendNewMessage(
        fcmDataMessage: FcmDataMessage
    )
}