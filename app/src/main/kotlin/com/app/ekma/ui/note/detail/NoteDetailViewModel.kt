package com.app.ekma.ui.note.detail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.ekma.alarm.AlarmEventsScheduler
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.CurrentEventsRefresher
import com.app.ekma.common.Data
import com.app.ekma.common.ProfileSingleton
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.Note
import com.app.ekma.data.models.service.INoteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val dataLocalManager: IDataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val noteService: INoteService
) : BaseViewModel() {
    override val TAG = NoteDetailViewModel::class.java.simpleName
    lateinit var note: Note

    fun onClickDeleteNote(
        context: Context,
        note: Note,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            noteService.deleteNote(note, ProfileSingleton().studentCode)
            noteService.deleteAudioNote(context, note.audioName, ProfileSingleton().studentCode)
            Data.getLocalNotesRuntime(noteService)
            callback()
        }
    }

    fun refreshDataInRecyclerView() {
        CurrentEventsRefresher.setData(true)
    }

    fun cancelAlarm(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val isNotify = dataLocalManager.getIsNotifyEvents()
            if (isNotify) {
                alarmEventsScheduler.cancelEvent(note)
            }
        }
    }
}