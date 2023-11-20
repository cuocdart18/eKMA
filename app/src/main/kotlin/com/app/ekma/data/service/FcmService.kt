package com.app.ekma.data.service

import com.app.ekma.data.models.FcmDataMessage
import com.app.ekma.data.models.repository.IFcmRepository
import com.app.ekma.data.models.repository.IProfileRepository
import com.app.ekma.data.models.service.IFcmService
import javax.inject.Inject

class FcmService @Inject constructor(
    private val profileRepository: IProfileRepository,
    private val fcmRepository: IFcmRepository
) : IFcmService {

    override suspend fun getFcmToken(code: String): String {
        return profileRepository.getFcmToken(code)
    }

    override suspend fun sendCallInvitationMessage(fcmDataMessage: FcmDataMessage) {
        fcmRepository.sendCallInvitationMessage(fcmDataMessage)
    }
}