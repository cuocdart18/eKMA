package com.example.kmatool.data.services

import com.example.kmatool.data.apis.ScheduleAPI
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.toPeriod
import com.example.kmatool.data.toProfile
import javax.inject.Inject

class ScheduleRemoteService @Inject constructor(
    private val scheduleAPI: ScheduleAPI
) {
    suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean
    ): List<Period> {
        val schedule = scheduleAPI.getPeriods(username, password, hashed)
        return schedule.periods.map { it.toPeriod() }
    }

    suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Profile {
        return scheduleAPI.getProfile(username, password, hashed).toProfile()
    }
}