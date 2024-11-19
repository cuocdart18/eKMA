package com.app.ekma.ui.score.details

import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.Resource
import com.app.ekma.common.gpaCalculator
import com.app.ekma.common.pattern.singleton.StudentScoreSingleton
import com.app.ekma.data.models.Student
import com.app.ekma.data.models.service.IScoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
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

    private val _autoScrollableBanner = MutableStateFlow(0)
    val autoScrollableBanner: StateFlow<Int>
        get() = _autoScrollableBanner.asStateFlow()
    val sizeOfBanner = 3

    fun startAutoScroll() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            while (isActive) {
                val nextPos = (_autoScrollableBanner.value + 1) % sizeOfBanner
                delay(4000L)
                _autoScrollableBanner.value = nextPos
            }
        }
    }

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