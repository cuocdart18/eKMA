package com.example.kmatool.common

import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.Period

object Data {
    // K = day
    // V = Periods/Notes on a day
    var periodsDayMap = mutableMapOf<String, List<Period>>()
    var notesDayMap = mutableMapOf<String, List<Note>>()
}