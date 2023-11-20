package com.app.ekma.data.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.data.models.Statistic
import com.app.ekma.data.models.StatisticSubject
import com.app.ekma.data.models.Student
import com.app.ekma.data.models.repository.IMiniStudentRepository
import com.app.ekma.data.models.repository.IStudentRepository
import com.app.ekma.data.models.service.IScoreService
import java.util.Calendar
import javax.inject.Inject

class ScoreService @Inject constructor(
    private val studentRepository: IStudentRepository,
    private val miniStudentRepository: IMiniStudentRepository
) : IScoreService {

    override suspend fun getStudentById(id: String): Resource<Student> {
        return studentRepository.getStudentById(id)
    }

    override suspend fun getStatisticSubjectById(
        subjectId: String,
        callback: (statisticSubject: StatisticSubject) -> Unit
    ) {
    }

    override suspend fun getStatistic(callback: (statistic: Statistic) -> Unit) {
    }

    override suspend fun getMiniStudentsByQuery(query: String): Resource<List<MiniStudent>> {
        return miniStudentRepository.getMiniStudentsByQuery(query)
    }

    override suspend fun getMiniStudents(): Resource<List<MiniStudent>> {
        return miniStudentRepository.getRecentMiniStudents()
    }

    override suspend fun insertMiniStudent(miniStudent: MiniStudent) {
        miniStudent.dateModified = Calendar.getInstance().time
        miniStudentRepository.insertMiniStudent(miniStudent)
    }
}