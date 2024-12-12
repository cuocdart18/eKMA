package com.app.ekma.data.models

data class FcmDataNewMessage(
    val token: String,
    val data: Map<String, Any>
)
