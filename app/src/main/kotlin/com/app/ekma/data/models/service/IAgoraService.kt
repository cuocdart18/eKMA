package com.app.ekma.data.models.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.AgoraTokenRequest
import com.app.ekma.data.models.AgoraTokenResponse

interface IAgoraService {

    suspend fun getToken(agoraTokenRequest: AgoraTokenRequest): Resource<AgoraTokenResponse>
}