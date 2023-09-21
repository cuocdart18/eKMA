package com.app.ekma.data.models

data class FcmDataMessage(
    val token: String,
    val data: Map<String, String>
)
