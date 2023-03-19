package com.example.kmatool.data.repositories

import com.example.kmatool.data.apis.ApiConfig
import com.example.kmatool.data.apis.ApiScheduleService
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.Schedule

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