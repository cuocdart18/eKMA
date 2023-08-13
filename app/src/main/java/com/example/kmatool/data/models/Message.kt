package com.example.kmatool.data.models

import java.util.Date

data class Message(
    val timestamp: Date,
    var content: String,
    var from: String,
)
