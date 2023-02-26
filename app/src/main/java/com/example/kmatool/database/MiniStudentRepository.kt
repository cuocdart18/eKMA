package com.example.kmatool.database

import android.content.Context
import com.example.kmatool.models.score.MiniStudent

class MiniStudentRepository(context: Context) : MiniStudentDao {
    private val db: MiniStudentDao = AppDatabase.getInstance(context).miniStudentDao()

    override suspend fun insertStudent(miniStudent: MiniStudent) {
        db.insertStudent(miniStudent)
    }

    override suspend fun getRecentHistorySearch(): List<MiniStudent> = db.getRecentHistorySearch()
}