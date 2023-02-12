package com.example.kmatool.api_service

class ScoreRepository : ApiService {
    override suspend fun getStatistics() =
        ApiConfig.apiService.getStatistics()

    override suspend fun getStudentStatistics(studentId: String) =
        ApiConfig.apiService.getStudentStatistics(studentId)

    override suspend fun getSubjectStatistics(subjectId: String) =
        ApiConfig.apiService.getSubjectStatistics(subjectId)

    override suspend fun getSubjects() =
        ApiConfig.apiService.getSubjects()

    override suspend fun search(data: String) =
        ApiConfig.apiService.search(data)
}