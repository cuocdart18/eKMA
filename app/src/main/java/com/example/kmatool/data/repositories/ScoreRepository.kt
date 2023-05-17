package com.example.kmatool.data.repositories

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.data.models.*
import com.example.kmatool.data.services.MiniStudentLocalService
import com.example.kmatool.data.services.ScoreRemoteService
import com.example.kmatool.common.OK
import com.example.kmatool.data.repository.toMiniStudent
import kotlinx.coroutines.coroutineScope
import java.util.*
import javax.inject.Inject

class ScoreRepository @Inject constructor(
    private val scoreRemoteService: ScoreRemoteService,
    private val miniStudentLocalService: MiniStudentLocalService
) : BaseRepositories() {
    override val TAG: String = ScoreRepository::class.java.simpleName

    suspend fun getDetailStudent(
        id: String,
        callback: (student: Student) -> Unit
    ) {
        coroutineScope {
            val result = scoreRemoteService.getStudentStatistics(id)
            callback(result)
        }
    }

    /*suspend fun getStatisticSubject(
        subjectId: String,
        callback: (statisticSubject: StatisticSubject) -> Unit
    ) {
        logDebug("get statistic subject")
        coroutineScope {
            val result = scoreRemoteService.getSubjectStatistics(subjectId)
            logDebug("getStatisticSubject status code = ${result.statusCode}")
            if (result.statusCode == OK) {
                val data = result.data
                logInfo("subject statistic data = $data")
                data?.let { callback(it) }
            }
        }
    }*/

    /*suspend fun getStatisticData(
        callback: (statistic: Statistic) -> Unit
    ) {
        coroutineScope {
            val result = scoreRemoteService.getStatistics()
            logDebug("getStatisticData status code = ${result.statusCode}")
            if (result.statusCode == OK) {
                val data = result.data
                logInfo("student statistic data = $data")
                data?.let { callback(it) }
            }
        }
    }*/

    suspend fun getSearchStudentData(
        text: String,
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        coroutineScope {
            val result = scoreRemoteService.getMiniStudents(text)
            if (result.statusCode == OK) {
                val data = result.data.map {
                    it.toMiniStudent()
                }
                callback(data)
            }
        }
    }

    suspend fun getListMiniStudentFromDatabase(
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        coroutineScope {
            val result = miniStudentLocalService.getRecentHistorySearch()
            callback(result)
        }
    }

    suspend fun saveMiniStudentsIntoDatabase(miniStudent: MiniStudent) {
        miniStudent.dateModified = Calendar.getInstance().time
        coroutineScope {
            miniStudentLocalService.insertStudent(miniStudent)
        }
    }
}
