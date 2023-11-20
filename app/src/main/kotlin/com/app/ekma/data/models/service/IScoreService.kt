package com.app.ekma.data.models.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.data.models.Statistic
import com.app.ekma.data.models.StatisticSubject
import com.app.ekma.data.models.Student

interface IScoreService {

    suspend fun getStudentById(id: String): Resource<Student>

    suspend fun getStatisticSubjectById(
        subjectId: String,
        callback: (statisticSubject: StatisticSubject) -> Unit
    )

    suspend fun getStatistic(
        callback: (statistic: Statistic) -> Unit
    )

    suspend fun getMiniStudentsByQuery(query: String): Resource<List<MiniStudent>>

    suspend fun getMiniStudents(): Resource<List<MiniStudent>>

    suspend fun insertMiniStudent(miniStudent: MiniStudent)
}