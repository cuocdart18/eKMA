package com.app.ekma.data.models

import java.util.Date

data class Message(
    val id: String,
    val timestamp: Date,
    var content: String,
    var from: String,
    val type: Int,
    var seen: MutableList<String>
) {
    var isLastSeenMessage = false
}