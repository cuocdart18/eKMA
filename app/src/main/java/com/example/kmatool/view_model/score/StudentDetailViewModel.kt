package com.example.kmatool.view_model.score

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmatool.api_service.ScoreRepository
import com.example.kmatool.models.score.Score
import com.example.kmatool.models.score.StatisticSubject
import com.example.kmatool.models.score.Student
import com.example.kmatool.utils.OK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentDetailViewModel : ViewModel() {
    private val TAG = SearchDataViewModel::class.java.simpleName
    private val scoreRepository = ScoreRepository()

    fun getDetailStudent(
        id: String,
        callback: (student: Student) -> Unit
    ) {
        Log.d(TAG, "get detail of student")
        // action
        viewModelScope.launch(Dispatchers.IO) {
            val result = scoreRepository.getStudentStatistics(id)
            Log.d(TAG, "getDetailStudent status code = ${result.statusCode}")

            withContext(Dispatchers.Main) {
                if (result.statusCode == OK) {
                    val data = result.data
                    Log.i(TAG, "data = $data")
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
        Log.d(TAG, "get statistic subject")
        // action
        viewModelScope.launch(Dispatchers.IO) {
            val id = score.subject.id
            val result = scoreRepository.getSubjectStatistics(id)
            Log.d(TAG, "get statistic subject status code = ${result.statusCode}")

            withContext(Dispatchers.Main) {
                if (result.statusCode == OK) {
                    val data = result.data
                    Log.i(TAG, "data = $data")
                    // update data to UI
                    data?.let { callback(it) }
                }
            }
        }
    }
}