package com.example.kmatool.data.services

import com.example.kmatool.data.data_source.apis.ScoreAPI
import com.example.kmatool.data.models.Student
import com.example.kmatool.data.repository.toStudent
import javax.inject.Inject

class ScoreRemoteService @Inject constructor(
    private val scoreAPI: ScoreAPI
) {
    /*suspend fun getStatistics() =
        scoreAPI.getStatistics()*/

    suspend fun getStudentStatistics(studentId: String): Student {
        val studentDto = scoreAPI.getStudentStatistics(studentId).pageProps.data
        return studentDto.toStudent()
    }

    /*suspend fun getSubjectStatistics(subjectId: String) =
        scoreAPI.getSubjectStatistics(subjectId)*/

    /*suspend fun getSubjects() =
        scoreAPI.getSubjects()*/

    suspend fun getMiniStudents(data: String) =
        scoreAPI.getMiniStudents(data)
}