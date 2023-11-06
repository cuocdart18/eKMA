package com.app.ekma.ui.score.details

import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.Resource
import com.app.ekma.common.StudentScoreSingleton
import com.app.ekma.common.gpaCalculator
import com.app.ekma.data.models.Student
import com.app.ekma.data.models.service.IScoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentDetailViewModel @Inject constructor(
    private val scoreService: IScoreService
) : BaseViewModel() {
    override val TAG = StudentDetailViewModel::class.java.simpleName

    var studentId: String = ""
    var isMyStudentId = false
    private lateinit var student: Student

    fun getDetailStudent(
        callback: (student: Student?) -> Unit
    ) {
        if (isMyStudentId && StudentScoreSingleton() != null) {
            callback(StudentScoreSingleton())
            return
        }
        if (!isMyStudentId && this::student.isInitialized) {
            callback(student)
            return
        }
        viewModelScope.launch {
            val studentRes = scoreService.getStudentById(studentId)
            if (studentRes is Resource.Success && studentRes.data != null) {
                student = studentRes.data
                student.avgScore = gpaCalculator(student.scores)
                if (isMyStudentId) {
                    StudentScoreSingleton.setData(student)
                }
                callback(student)
            } else {
                callback(null)
            }
        }
    }
}