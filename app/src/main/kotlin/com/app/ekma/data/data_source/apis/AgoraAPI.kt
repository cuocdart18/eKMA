package com.app.ekma.data.data_source.apis

import com.app.ekma.data.data_source.apis.dto.AgoraTokenRequestDto
import com.app.ekma.data.data_source.apis.dto.AgoraTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AgoraAPI {

    @Headers("Content-Type: application/json")
    @POST("/getToken")
    suspend fun getToken(
        @Body body: AgoraTokenRequestDto
    ): AgoraTokenResponseDto
}