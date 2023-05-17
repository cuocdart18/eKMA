package com.example.kmatool.ui.note.detail

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.data.app_data.DataLocalManager
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val dataLocalManager: DataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler
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

    fun cancelAlarm(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val isNotify = dataLocalManager.getIsNotifyEventsSPref()
            if (isNotify) {
                alarmEventsScheduler.cancelEvent(note)
            }
        }
    }
}