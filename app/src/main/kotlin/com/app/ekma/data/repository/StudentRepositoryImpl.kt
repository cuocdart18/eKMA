package com.app.ekma.data.repository

import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.common.Resource
import com.app.ekma.data.data_source.apis.ScoreAPI
import com.app.ekma.data.models.Student
import com.app.ekma.data.models.repository.IStudentRepository
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