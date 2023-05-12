package com.example.kmatool.ui.score.search

import androidx.databinding.ObservableField
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.ScoreRepository
import com.example.kmatool.data.models.MiniStudent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchDataViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository
) : BaseViewModel() {
    override val TAG = SearchDataViewModel::class.java.simpleName

    // data layer UI
    var isUserTyped = ObservableField<Boolean>()

    // instant EditText
    @OptIn(ObsoleteCoroutinesApi::class)
    internal val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    @OptIn(ObsoleteCoroutinesApi::class, FlowPreview::class)
    private val internalSearchResult = queryChannel
        .asFlow()
        .debounce(600L)
        .filterNot { it.isBlank() }
        .distinctUntilChanged()
    val searchResult = internalSearchResult.asLiveData()

    fun onSearchEditTextObserved(
        text: String,
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        isUserTyped.set(true)
        makeCallApi(text, callback)
    }

    private fun makeCallApi(
        text: String,
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.getSearchStudentData(text) { result ->
                CoroutineScope(Dispatchers.Main).launch {
                    callback(result)
                }
            }
        }
    }

    fun showRecentSearchHistory(
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.getListMiniStudentFromDatabase { result ->
                CoroutineScope(Dispatchers.Main).launch {
                    callback(result)
                }
            }
        }
    }

    fun insertMiniStudentToDb(miniStudent: MiniStudent) {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.saveMiniStudentsIntoDatabase(miniStudent)
        }
    }
}
