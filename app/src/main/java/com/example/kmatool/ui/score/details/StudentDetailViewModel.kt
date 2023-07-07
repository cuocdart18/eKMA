package com.example.kmatool.ui.score.details

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.Resource
import com.example.kmatool.common.gpaCalculator
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

    var studentId: String = ""
    var isMyStudentId = false
    private lateinit var student: Student

    fun getDetailStudent(
        callback: (student: Student?) -> Unit
    ) {
        if (isMyStudentId && Data.myStudentInfo != null) {
            callback(Data.myStudentInfo)
            return
        }
        if (!isMyStudentId && this::student.isInitialized) {
            callback(student)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val studentRes = scoreService.getStudentById(studentId)
            withContext(Dispatchers.Default) {
                if (studentRes is Resource.Success && studentRes.data != null) {
                    student = studentRes.data
                    student.avgScore = gpaCalculator(student.scores)

                    if (isMyStudentId) {
                        Data.myStudentInfo = student
                    }

                    withContext(Dispatchers.Main) { callback(student) }
                } else {
                    withContext(Dispatchers.Main) { callback(null) }
                }
            }
        }
    }
}