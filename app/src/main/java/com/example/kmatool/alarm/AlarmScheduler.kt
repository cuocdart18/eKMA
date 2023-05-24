package com.example.kmatool.alarm

import com.example.kmatool.data.models.Event

interface AlarmScheduler {
    fun scheduleEvent(event: Event)

    fun cancelEvent(event: Event)
}