package com.app.ekma.data.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.AgoraTokenRequest
import com.app.ekma.data.models.AgoraTokenResponse
import com.app.ekma.data.models.repository.IAgoraTokenRepository
import com.app.ekma.data.models.service.IAgoraService
import javax.inject.Inject

class AgoraService @Inject constructor(
    private val agoraTokenRepository: IAgoraTokenRepository
) : IAgoraService {

    override suspend fun getToken(agoraTokenRequest: AgoraTokenRequest): Resource<AgoraTokenResponse> {
        return agoraTokenRepository.getToken(agoraTokenRequest)
    }
}