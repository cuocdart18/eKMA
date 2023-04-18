package com.example.kmatool.data.models

interface Event {
    val type: Int

    fun getTimeCompare(): String
}