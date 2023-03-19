package com.example.kmatool.data.repositories

import android.content.Context
import com.example.kmatool.data.database.AppDatabase
import com.example.kmatool.data.database.daos.MiniStudentDao
import com.example.kmatool.data.models.MiniStudent

class MiniStudentRepository(context: Context) : MiniStudentDao {
    private val db: MiniStudentDao = AppDatabase.getInstance(context).miniStudentDao()

    override suspend fun insertStudent(miniStudent: MiniStudent) {
        db.insertStudent(miniStudent)
    }

    override suspend fun getRecentHistorySearch(): List<MiniStudent> = db.getRecentHistorySearch()
}