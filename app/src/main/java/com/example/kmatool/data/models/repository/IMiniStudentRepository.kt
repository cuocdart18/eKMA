package com.example.kmatool.data.models.repository

import com.example.kmatool.common.Resource
import com.example.kmatool.data.models.MiniStudent

interface IMiniStudentRepository {

    suspend fun getMiniStudentsByQuery(query: String): Resource<List<MiniStudent>>

    suspend fun insertMiniStudent(miniStudent: MiniStudent)

    suspend fun getRecentMiniStudents(): Resource<List<MiniStudent>>

    suspend fun deleteRecentMiniStudents()
}