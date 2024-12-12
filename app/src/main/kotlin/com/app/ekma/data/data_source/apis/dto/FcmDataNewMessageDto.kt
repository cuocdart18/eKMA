package com.app.ekma.data.data_source.apis.dto

data class FcmDataNewMessageDto(
    val token: String,
    val data: Map<String, Any>
)
