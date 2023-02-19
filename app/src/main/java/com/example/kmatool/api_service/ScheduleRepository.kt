package com.example.kmatool.api_service

import com.example.kmatool.models.schedule.Schedule

class ScheduleRepository : ApiScheduleService {
    override suspend fun getScheduleData(
        username: String,
        password: String,
        hashed: Boolean
    ): Schedule =
        ApiConfig.apiScheduleService.getScheduleData(username, password, hashed)
}