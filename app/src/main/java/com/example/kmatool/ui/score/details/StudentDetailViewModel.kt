package com.example.kmatool.ui.score.details

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.ScoreRepository
import com.example.kmatool.data.models.Score
import com.example.kmatool.data.models.StatisticSubject
import com.example.kmatool.data.models.Student
import com.example.kmatool.utils.OK
import com.example.kmatool.ui.score.search.SearchDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentDetailViewModel : BaseViewModel() {
    override val TAG = SearchDataViewModel::class.java.simpleName
    private val scoreRepository = ScoreRepository()

    fun getDetailStudent(
        id: String,
        callback: (student: Student) -> Unit
    ) {
        logDebug("get detail of student")
        // action
        viewModelScope.launch(Dispatchers.IO) {
            val result = scoreRepository.getStudentStatistics(id)
            logDebug("getDetailStudent status code = ${result.statusCode}")

            withContext(Dispatchers.Main) {
                if (result.statusCode == OK) {
                    val data = result.data
                    logInfo("data = $data")
                    // update data to UI
                    data?.let { callback(it) }
                }
            }
        }
    }

    fun getStatisticSubject(
        score: Score,
        callback: (statisticSubject: StatisticSubject) -> Unit
    ) {
        logDebug("get statistic subject")
        // action
        viewModelScope.launch(Dispatchers.IO) {
            val id = score.subject.id
            val result = scoreRepository.getSubjectStatistics(id)
            logDebug("get statistic subject status code = ${result.statusCode}")

            withContext(Dispatchers.Main) {
                if (result.statusCode == OK) {
                    val data = result.data
                    logInfo("data = $data")
                    // update data to UI
                    data?.let { callback(it) }
                }
            }
        }
    }
}