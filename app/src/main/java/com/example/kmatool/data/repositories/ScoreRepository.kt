package com.example.kmatool.data.repositories

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.data.models.*
import com.example.kmatool.data.services.MiniStudentLocalService
import com.example.kmatool.data.services.ScoreRemoteService
import com.example.kmatool.utils.OK
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
        logDebug("get detail of student with id=$id")
        coroutineScope {
            val result = scoreRemoteService.getStudentStatistics(id)
            if (result.__N_SSP) {
                val student = result.pageProps.data
                logDebug("student=$student")
                callback(student)
            }
        }
    }

    suspend fun getStatisticSubject(
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
    }

    suspend fun getStatisticData(
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
    }

    suspend fun getSearchStudentData(
        text: String,
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        logDebug("START call api with text = $text")
        coroutineScope {
            val result = scoreRemoteService.search(text)
            logDebug("makeCallApi score status code = ${result.statusCode}")
            if (result.statusCode == OK) {
                val data = result.data
                logInfo("search student data = $data")
                data?.let { callback(it) }
            }
        }
    }

    suspend fun getListMiniStudentFromDatabase(
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        logDebug("get list student from Db")
        coroutineScope {
            val result = miniStudentLocalService.getRecentHistorySearch()
            logInfo("list miniStudent recently = $result")
            callback(result)
        }
    }

    suspend fun saveMiniStudentsIntoDatabase(miniStudent: MiniStudent) {
        logDebug("update student id = ${miniStudent.id}")
        miniStudent.dateModified = Calendar.getInstance().time
        coroutineScope {
            miniStudentLocalService.insertStudent(miniStudent)
            logDebug("complete insert student = $miniStudent")
        }
    }
}
