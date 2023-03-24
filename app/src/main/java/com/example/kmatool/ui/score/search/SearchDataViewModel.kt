package com.example.kmatool.ui.score.search

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.ScoreRepository
import com.example.kmatool.data.repositories.MiniStudentRepository
import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.utils.OK
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class SearchDataViewModel : BaseViewModel() {
    override val TAG = SearchDataViewModel::class.java.simpleName
    private val scoreRepository = ScoreRepository()

    // data layer UI
    var isUserTyped = ObservableField<Boolean>()

    // instant EditText
    internal val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)
    private val internalSearchResult = queryChannel
        .asFlow()
        .debounce(500L)
        .filterNot { it.isBlank() }
        .distinctUntilChanged()
    val searchResult = internalSearchResult.asLiveData()

    fun onSearchEditTextObserved(
        text: String,
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        isUserTyped.set(true)
    }

    fun showRecentSearchHistory(
        context: Context,
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        getListMiniStudentFromDatabase(context, callback)
    }

    fun insertMiniStudentToDb(context: Context, miniStudent: MiniStudent) {
        saveMiniStudentsIntoDatabase(context, miniStudent)
    }

    // call API with fresh query
    private fun makeCallApi(
        text: String,
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        logDebug("START call api with text = $text")
        // action
        viewModelScope.launch(Dispatchers.IO) {
            val result = scoreRepository.search(text)
            logDebug("makeCallApi score status code = ${result.statusCode}")

            withContext(Dispatchers.Main) {
                if (result.statusCode == OK) {
                    val data = result.data
                    logInfo("search student data = $data")
                    // update data to UI, pass to fragment
                    data?.let {
                        callback(it)
                    }
                }
            }
        }
    }

    // get data from Db
    private fun getListMiniStudentFromDatabase(
        context: Context,
        callback: (ministudents: List<MiniStudent>) -> Unit
    ) {
        logDebug("get list student from Db")
        // action
        val miniStudentRepository = MiniStudentRepository(context)
        viewModelScope.launch(Dispatchers.IO) {
            val result = miniStudentRepository.getRecentHistorySearch()

            withContext(Dispatchers.Main) {
                logInfo("list miniStudent recently = $result")
                // update data to UI, pass to fragment
                callback(result)
            }
        }
    }

    // set date modified and update in database
    private fun saveMiniStudentsIntoDatabase(context: Context, miniStudent: MiniStudent) {
        logDebug("update student id = ${miniStudent.id}")
        // action
        miniStudent.dateModified = Calendar.getInstance().time

        val miniStudentRepository = MiniStudentRepository(context)
        viewModelScope.launch(Dispatchers.IO) {
            miniStudentRepository.insertStudent(miniStudent)
            logDebug("complete insert student = $miniStudent")
        }
    }
}
