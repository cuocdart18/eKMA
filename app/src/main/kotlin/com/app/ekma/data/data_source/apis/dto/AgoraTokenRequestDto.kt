package com.app.ekma.data.data_source.apis.dto

data class AgoraTokenRequestDto(
    val tokenType: String,
    val channel: String,
    val role: String,
    val uid: String,
    val expired: Int
)
