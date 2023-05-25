package com.example.kmatool.data.repository

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.common.Resource
import com.example.kmatool.data.data_source.apis.ScoreAPI
import com.example.kmatool.data.models.Student
import com.example.kmatool.data.models.repository.IStudentRepository
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val scoreAPI: ScoreAPI
) : BaseRepositories(), IStudentRepository {

    override suspend fun getStudentById(studentId: String): Resource<Student> {
        return safeApiCall {
            scoreAPI.getStudentStatistics(studentId).pageProps.data.toStudent()
        }
    }
}