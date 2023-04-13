package com.example.kmatool.data.services

import com.example.kmatool.data.apis.ScoreAPI
import javax.inject.Inject

class ScoreRemoteService @Inject constructor(
    private val scoreAPI: ScoreAPI
) {
    suspend fun getStatistics() =
        scoreAPI.getStatistics()

    suspend fun getStudentStatistics(studentId: String) =
        scoreAPI.getStudentStatistics(studentId)

    suspend fun getSubjectStatistics(subjectId: String) =
        scoreAPI.getSubjectStatistics(subjectId)

    suspend fun getSubjects() =
        scoreAPI.getSubjects()

    suspend fun search(data: String) =
        scoreAPI.search(data)
}