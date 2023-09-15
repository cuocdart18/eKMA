package com.app.ekma.ui.score.search

import androidx.databinding.ObservableField
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.Resource
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.data.models.service.IScoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchDataViewModel @Inject constructor(
    private val scoreService: IScoreService
) : BaseViewModel() {
    override val TAG = SearchDataViewModel::class.java.simpleName

    // data layer UI
    var isUserTyped = ObservableField<Boolean>()

    // instant EditText
    @OptIn(ObsoleteCoroutinesApi::class)
    internal val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    @OptIn(ObsoleteCoroutinesApi::class, FlowPreview::class)
    val searchResult = queryChannel
        .asFlow()
        .debounce(600L)
        .filterNot { it.isBlank() }
        .distinctUntilChanged()
        .asLiveData()

    fun onSearchEditTextObserved(
        text: String,
        callback: (miniStudents: List<MiniStudent>?) -> Unit
    ) {
        isUserTyped.set(true)
        makeCallApi(text, callback)
    }

    private fun makeCallApi(
        text: String,
        callback: (miniStudents: List<MiniStudent>?) -> Unit
    ) {
        viewModelScope.launch {
            val miniStudentsRes = scoreService.getMiniStudentsByQuery(text)
            if (miniStudentsRes is Resource.Success && miniStudentsRes.data != null) {
                callback(miniStudentsRes.data)
            } else {
                callback(null)
            }
        }
    }

    fun showRecentSearchHistory(
        callback: (miniStudents: List<MiniStudent>?) -> Unit
    ) {
        viewModelScope.launch {
            val miniStudentsRes = scoreService.getMiniStudents()
            if (miniStudentsRes is Resource.Success && miniStudentsRes.data != null) {
                callback(miniStudentsRes.data)
            } else {
                callback(null)
            }
        }
    }

    fun insertMiniStudentToDb(miniStudent: MiniStudent) {
        viewModelScope.launch {
            scoreService.insertMiniStudent(miniStudent)
        }
    }
}
