package com.app.ekma.data.repository

import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.common.Resource
import com.app.ekma.data.data_source.apis.AgoraAPI
import com.app.ekma.data.models.AgoraTokenRequest
import com.app.ekma.data.models.AgoraTokenResponse
import com.app.ekma.data.models.repository.IAgoraTokenRepository
import javax.inject.Inject

class AgoraTokenRepositoryImpl @Inject constructor(
    private val agoraAPI: AgoraAPI
) : BaseRepositories(), IAgoraTokenRepository {

    override suspend fun getToken(agoraTokenRequest: AgoraTokenRequest): Resource<AgoraTokenResponse> {
        return safeApiCall {
            agoraAPI.getToken(agoraTokenRequest.toAgoraTokenRequestDto()).toAgoraTokenResponse()
        }
    }
}