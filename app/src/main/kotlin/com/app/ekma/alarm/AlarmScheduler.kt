package com.app.ekma.alarm

import com.app.ekma.data.models.Event

interface AlarmScheduler {
    fun scheduleEvent(event: Event)

    fun cancelEvent(event: Event)
}