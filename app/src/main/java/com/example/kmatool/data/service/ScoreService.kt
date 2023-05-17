package com.example.kmatool.data.service

import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.data.models.Statistic
import com.example.kmatool.data.models.StatisticSubject
import com.example.kmatool.data.models.Student
import com.example.kmatool.data.models.repository.IMiniStudentRepository
import com.example.kmatool.data.models.repository.IStudentRepository
import com.example.kmatool.data.models.service.IScoreService
import java.util.Calendar
import javax.inject.Inject

class ScoreService @Inject constructor(
    private val studentRepository: IStudentRepository,
    private val miniStudentRepository: IMiniStudentRepository
) : IScoreService {

    override suspend fun getStudentById(id: String): Student {
        return studentRepository.getStudentById(id)
    }

    override suspend fun getStatisticSubjectById(
        subjectId: String,
        callback: (statisticSubject: StatisticSubject) -> Unit
    ) {
    }

    override suspend fun getStatistic(callback: (statistic: Statistic) -> Unit) {
    }

    override suspend fun getMiniStudentsByQuery(query: String): List<MiniStudent> {
        return miniStudentRepository.getMiniStudentsByQuery(query)
    }

    override suspend fun getMiniStudents(): List<MiniStudent> {
        return miniStudentRepository.getRecentMiniStudents()
    }

    override suspend fun insertMiniStudent(miniStudent: MiniStudent) {
        miniStudent.dateModified = Calendar.getInstance().time
        miniStudentRepository.insertMiniStudent(miniStudent)
    }
}