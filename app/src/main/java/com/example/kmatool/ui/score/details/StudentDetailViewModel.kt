package com.example.kmatool.ui.score.details

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Resource
import com.example.kmatool.data.models.Student
import com.example.kmatool.data.models.service.IScoreService
import com.example.kmatool.ui.score.search.SearchDataViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StudentDetailViewModel @Inject constructor(
    private val scoreService: IScoreService
) : BaseViewModel() {
    override val TAG = SearchDataViewModel::class.java.simpleName

    private lateinit var student: Student

    fun getDetailStudent(
        id: String,
        callback: (student: Student?) -> Unit
    ) {
        if (this::student.isInitialized) {
            callback(student)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val studentRes = scoreService.getStudentById(id)
            withContext(Dispatchers.Main) {
                if (studentRes is Resource.Success && studentRes.data != null) {
                    student = studentRes.data
                    callback(student)
                } else {
                    callback(null)
                }
            }
        }
    }

    /*fun getStatisticSubject(
        score: Score,
        callback: (statisticSubject: StatisticSubject) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = score.subject.id
            scoreRepository.getStatisticSubject(id) { result ->
                CoroutineScope(Dispatchers.Main).launch {
                    // update data to UI
                    callback(result)
                }
            }
        }
    }*/
}