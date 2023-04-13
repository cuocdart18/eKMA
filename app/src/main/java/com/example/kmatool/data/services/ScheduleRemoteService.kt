package com.example.kmatool.data.services

import com.example.kmatool.data.apis.ScheduleAPI
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.Schedule
import javax.inject.Inject

class ScheduleRemoteService @Inject constructor(
    private val scheduleAPI: ScheduleAPI
) {
    suspend fun getScheduleData(
        username: String,
        password: String,
        hashed: Boolean
    ): Schedule =
        scheduleAPI.getScheduleData(username, password, hashed)

    suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Profile =
        scheduleAPI.getProfile(username, password, hashed)
}