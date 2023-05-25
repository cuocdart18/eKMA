package com.example.kmatool.data.models.repository

import com.example.kmatool.common.Resource
import com.example.kmatool.data.models.Student

interface IStudentRepository {

    suspend fun getStudentById(studentId: String): Resource<Student>
}