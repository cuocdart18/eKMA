package com.app.ekma.data.models.repository

import com.app.ekma.common.Resource
import com.app.ekma.data.models.AgoraTokenRequest
import com.app.ekma.data.models.AgoraTokenResponse

interface IAgoraTokenRepository {

    suspend fun getToken(agoraTokenRequest: AgoraTokenRequest): Resource<AgoraTokenResponse>
}