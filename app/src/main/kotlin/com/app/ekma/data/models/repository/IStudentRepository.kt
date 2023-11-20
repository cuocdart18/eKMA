package com.app.ekma.data.models.repository

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Student

interface IStudentRepository {

    suspend fun getStudentById(studentId: String): Resource<Student>
}