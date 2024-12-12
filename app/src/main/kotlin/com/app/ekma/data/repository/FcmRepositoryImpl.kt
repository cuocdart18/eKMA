package com.app.ekma.data.repository

import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.data.data_source.apis.EKmaAPI
import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.repository.IFcmRepository
import javax.inject.Inject

class FcmRepositoryImpl @Inject constructor(
    private val eKmaApi: EKmaAPI
) : BaseRepositories(), IFcmRepository {

    override suspend fun sendCallInvitationMessage(fcmDataMessage: FcmDataMessage) {
        safeApiCall {
            eKmaApi.sendCallInvitationMessage(fcmDataMessage.toFcmDataMessageDto())
        }
    }

    override suspend fun sendNewMessage(fcmDataMessage: FcmDataMessage) {
        safeApiCall {
            eKmaApi.sendNewMessage(fcmDataMessage.toFcmDataMessageDto())
        }
    }
}