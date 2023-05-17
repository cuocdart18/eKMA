package com.example.kmatool.data.repository

import com.example.kmatool.data.data_source.apis.ScoreAPI
import com.example.kmatool.data.models.Student
import com.example.kmatool.data.models.repository.IStudentRepository
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val scoreAPI: ScoreAPI
) : IStudentRepository {

    override suspend fun getStudentById(studentId: String): Student {
        val studentDto = scoreAPI.getStudentStatistics(studentId).pageProps.data
        return studentDto.toStudent()
    }
}