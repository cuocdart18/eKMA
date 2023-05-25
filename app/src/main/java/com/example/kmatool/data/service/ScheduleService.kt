package com.example.kmatool.data.service

import com.example.kmatool.common.Resource
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.repository.IPeriodRepository
import com.example.kmatool.data.models.service.IScheduleService
import javax.inject.Inject

class ScheduleService @Inject constructor(
    private val periodRepository: IPeriodRepository
) : IScheduleService {

    override suspend fun deletePeriods() {
        periodRepository.deletePeriods()
    }

    override suspend fun insertPeriods(periods: List<Period>) {
        periodRepository.insertPeriods(periods)
    }

    override suspend fun getPeriods(): Resource<List<Period>> {
        return periodRepository.getPeriods()
    }

    override suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<List<Period>> {
        return periodRepository.getPeriods(username, password, hashed)
    }
}