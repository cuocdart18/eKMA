package com.example.kmatool.data.models

import java.util.Date

data class ChatRoom(
    var id: String,
    var name: String,
    val members: List<String>,
    var timestamp: Date
) {

}
