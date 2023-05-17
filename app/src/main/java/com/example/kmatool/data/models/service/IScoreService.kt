package com.example.kmatool.data.models.service

import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.data.models.Statistic
import com.example.kmatool.data.models.StatisticSubject
import com.example.kmatool.data.models.Student

interface IScoreService {

    suspend fun getStudentById(id: String): Student

    suspend fun getStatisticSubjectById(
        subjectId: String,
        callback: (statisticSubject: StatisticSubject) -> Unit
    )

    suspend fun getStatistic(
        callback: (statistic: Statistic) -> Unit
    )

    suspend fun getMiniStudentsByQuery(query: String): List<MiniStudent>

    suspend fun getMiniStudents(): List<MiniStudent>

    suspend fun insertMiniStudent(miniStudent: MiniStudent)
}