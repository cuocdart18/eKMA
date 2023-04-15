package com.example.kmatool.common

import androidx.lifecycle.MutableLiveData
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.models.Period

object Data {
    val periodsDayMap = MutableLiveData<Map<String, List<Period>>>()
    val notesDayMap = MutableLiveData<Map<String, List<Note>>>()
}