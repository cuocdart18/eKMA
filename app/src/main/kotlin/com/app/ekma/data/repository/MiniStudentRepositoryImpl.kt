package com.app.ekma.data.repository

import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.common.Resource
import com.app.ekma.data.data_source.apis.ScoreAPI
import com.app.ekma.data.data_source.database.daos.MiniStudentDao
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.data.models.repository.IMiniStudentRepository
import javax.inject.Inject

class MiniStudentRepositoryImpl @Inject constructor(
    private val miniStudentDao: MiniStudentDao,
    private val scoreAPI: ScoreAPI
) : BaseRepositories(), IMiniStudentRepository {

    override suspend fun getMiniStudentsByQuery(query: String): Resource<List<MiniStudent>> {
        return safeApiCall {
            scoreAPI.getMiniStudents(query).data.map { it.toMiniStudent() }
        }
    }

    override suspend fun insertMiniStudent(miniStudent: MiniStudent) {
        miniStudentDao.insertStudent(miniStudent.toMiniStudentEntity())
    }

    override suspend fun getRecentMiniStudents(): Resource<List<MiniStudent>> {
        return safeDaoCall {
            miniStudentDao.getRecentHistorySearch().map { it.toMiniStudent() }
        }
    }

    override suspend fun deleteRecentMiniStudents() {
        miniStudentDao.deleteRecentHistorySearch()
    }
}