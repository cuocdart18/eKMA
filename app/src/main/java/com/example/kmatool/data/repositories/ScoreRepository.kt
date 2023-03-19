package com.example.kmatool.data.repositories

import com.example.kmatool.data.apis.ApiConfig
import com.example.kmatool.data.apis.ApiScoreService

class ScoreRepository : ApiScoreService {
    override suspend fun getStatistics() =
        ApiConfig.apiScoreService.getStatistics()

    override suspend fun getStudentStatistics(studentId: String) =
        ApiConfig.apiScoreService.getStudentStatistics(studentId)

    override suspend fun getSubjectStatistics(subjectId: String) =
        ApiConfig.apiScoreService.getSubjectStatistics(subjectId)

    override suspend fun getSubjects() =
        ApiConfig.apiScoreService.getSubjects()

    override suspend fun search(data: String) =
        ApiConfig.apiScoreService.search(data)
}