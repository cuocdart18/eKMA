package com.app.ekma.data.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Period
import com.app.ekma.data.models.repository.IPeriodRepository
import com.app.ekma.data.models.service.IScheduleService
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

    override suspend fun getPeriods(
        username: String,
        password: String,
        hashed: Boolean,
        semesterCode: String
    ): Resource<List<Period>> {
        return periodRepository.getPeriods(username, password, hashed, semesterCode)
    }

    override suspend fun getSemesterCodes(): Resource<List<String>> {
        return periodRepository.getSemesterCodes()
    }
}