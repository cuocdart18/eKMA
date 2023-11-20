package com.app.ekma.data.models.repository

import com.app.ekma.common.Resource
import com.app.ekma.data.models.MiniStudent

interface IMiniStudentRepository {

    suspend fun getMiniStudentsByQuery(query: String): Resource<List<MiniStudent>>

    suspend fun insertMiniStudent(miniStudent: MiniStudent)

    suspend fun getRecentMiniStudents(): Resource<List<MiniStudent>>

    suspend fun deleteRecentMiniStudents()
}