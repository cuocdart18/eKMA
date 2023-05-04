package com.example.kmatool.ui.note.detail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.common.DataStoreManager
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {
    override val TAG = NoteDetailViewModel::class.java.simpleName

    fun onClickDeleteNote(
        note: Note,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNote(note) {
                CoroutineScope(Dispatchers.IO).launch {
                    noteRepository.updateLocalDataRuntime()
                    withContext(Dispatchers.Main) {
                        callback()
                    }
                }
            }
        }
    }

    fun refreshDataInRecyclerView() {
        Data.isRefreshClickedEvents.value = true
    }

    fun cancelAlarm(context: Context, note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.isNotifyEventsDataStoreFlow.collect() { state ->
                if (state) {
                    AlarmEventsScheduler(context).cancelEvent(note)
                    logDebug("cancel notify event state=$state")
                }
                cancel()
            }
        }
    }
}