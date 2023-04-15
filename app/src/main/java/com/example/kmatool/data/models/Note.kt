package com.example.kmatool.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    var title: String,
    var content: String? = null,
    var date: String,
    var time: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}