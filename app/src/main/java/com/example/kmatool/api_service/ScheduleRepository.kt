package com.example.kmatool.api_service

import com.example.kmatool.models.schedule.Profile
import com.example.kmatool.models.schedule.Schedule
import kotlinx.coroutines.Deferred

class ScheduleRepository : ApiScheduleService {
    override suspend fun getScheduleData(
        username: String,
        password: String,
        hashed: Boolean
    ): Schedule =
        ApiConfig.apiScheduleService.getScheduleData(username, password, hashed)

    override suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Profile =
        ApiConfig.apiScheduleService.getProfile(username, password, hashed)
}