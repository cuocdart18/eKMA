package com.app.ekma.data.models

import java.util.Date

data class ChatRoom(
    var id: String,
    var name: String,
    val members: List<String>,
    var timestamp: Date,
    var content: String,
    var from: String,
    var type: Int = 1,
    var seenMembers: MutableList<String>
) {
    var isOnline = false
}