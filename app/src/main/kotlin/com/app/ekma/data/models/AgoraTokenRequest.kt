package com.app.ekma.data.models

data class AgoraTokenRequest(
    val tokenType: String,
    val channel: String,
    val role: String,
    val uid: String,
    val expired: Int
)
