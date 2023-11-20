package com.app.ekma.data.data_source.apis.dto

data class FcmDataMessageDto(
    val token: String,
    val data: Map<String, String>
)
