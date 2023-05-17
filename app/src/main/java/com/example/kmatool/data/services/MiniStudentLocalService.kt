package com.example.kmatool.data.services

import com.example.kmatool.data.database.daos.MiniStudentDao
import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.data.toMiniStudent
import com.example.kmatool.data.toMiniStudentEntity
import javax.inject.Inject

class MiniStudentLocalService @Inject constructor(
    private val miniStudentDao: MiniStudentDao
) {
    suspend fun insertStudent(miniStudent: MiniStudent) {
        miniStudentDao.insertStudent(miniStudent.toMiniStudentEntity())
    }

    suspend fun getRecentHistorySearch() =
        miniStudentDao.getRecentHistorySearch().map { it.toMiniStudent() }

    suspend fun deleteRecentHistorySearch() {
        miniStudentDao.deleteRecentHistorySearch()
    }
}