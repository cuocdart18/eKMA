package com.example.kmatool.common

import com.example.kmatool.data.models.Event

interface AlarmScheduler {
    fun scheduleEvents(event: Event)

    fun cancel(event: Event)
}