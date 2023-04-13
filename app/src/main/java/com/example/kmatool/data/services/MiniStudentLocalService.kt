package com.example.kmatool.data.services

import com.example.kmatool.data.database.daos.MiniStudentDao
import com.example.kmatool.data.models.MiniStudent
import javax.inject.Inject

class MiniStudentLocalService @Inject constructor(
    private val miniStudentDao: MiniStudentDao
) {
    suspend fun insertStudent(miniStudent: MiniStudent) {
        miniStudentDao.insertStudent(miniStudent)
    }

    suspend fun getRecentHistorySearch(): List<MiniStudent> = miniStudentDao.getRecentHistorySearch()
}