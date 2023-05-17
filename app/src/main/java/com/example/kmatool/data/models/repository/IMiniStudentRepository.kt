package com.example.kmatool.data.models.repository

import com.example.kmatool.data.models.MiniStudent

interface IMiniStudentRepository {

    suspend fun getMiniStudentsByQuery(query: String): List<MiniStudent>

    suspend fun insertMiniStudent(miniStudent: MiniStudent)

    suspend fun getRecentMiniStudents(): List<MiniStudent>

    suspend fun deleteRecentMiniStudents()
}