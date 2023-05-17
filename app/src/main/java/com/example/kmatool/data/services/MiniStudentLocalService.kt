package com.example.kmatool.data.services

import com.example.kmatool.data.data_source.database.daos.MiniStudentDao
import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.data.repository.toMiniStudent
import com.example.kmatool.data.repository.toMiniStudentEntity
import javax.inject.Inject

class MiniStudentLocalService @Inject constructor(
    private val miniStudentDao: MiniStudentDao
) {
    suspend fun insertStudent(miniStudent: MiniStudent) {
        miniStudentDao.insertStudent(miniStudent.toMiniStudentEntity())
    }

    suspend fun getRecentHistorySearch(): List<MiniStudent> =
        miniStudentDao.getRecentHistorySearch().map { it.toMiniStudent() }

    suspend fun deleteRecentHistorySearch() {
        miniStudentDao.deleteRecentHistorySearch()
    }
}