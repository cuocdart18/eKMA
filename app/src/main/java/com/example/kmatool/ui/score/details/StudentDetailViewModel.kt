package com.example.kmatool.ui.score.details

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.ScoreRepository
import com.example.kmatool.data.models.Score
import com.example.kmatool.data.models.StatisticSubject
import com.example.kmatool.data.models.Student
import com.example.kmatool.utils.OK
import com.example.kmatool.ui.score.search.SearchDataViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StudentDetailViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository
) : BaseViewModel() {
    override val TAG = SearchDataViewModel::class.java.simpleName

    fun getDetailStudent(
        id: String,
        callback: (student: Student) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.getDetailStudent(id) { result ->
                CoroutineScope(Dispatchers.Main).launch {
                    // update data to UI
                    callback(result)
                }
            }
        }
    }

    fun getStatisticSubject(
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
    }
}