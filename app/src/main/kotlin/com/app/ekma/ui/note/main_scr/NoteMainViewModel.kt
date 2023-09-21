package com.app.ekma.ui.note.main_scr

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.ekma.alarm.AlarmEventsScheduler
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.ADD_NOTE_MODE
import com.app.ekma.common.Data
import com.app.ekma.common.PAUSE_RECORDING
import com.app.ekma.common.UPDATE_NOTE_MODE
import com.app.ekma.common.copy
import com.app.ekma.common.formatDoubleChar
import com.app.ekma.common.getPersistentRecordDirPath
import com.app.ekma.common.toDayMonthYear
import com.app.ekma.common.toHourMinute
import com.app.ekma.common.toMilli
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.Note
import com.app.ekma.data.models.service.INoteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class NoteMainViewModel @Inject constructor(
    private val dataLocalManager: IDataLocalManager,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val noteService: INoteService
) : BaseViewModel() {
    override val TAG: String = NoteMainViewModel::class.java.simpleName
    val selectDay = MutableLiveData<String>()
    val selectTime = MutableLiveData<String>()

    // default mode
    var noteMode = ADD_NOTE_MODE

    // update note mode
    lateinit var oldNote: Note

    // voice note mode
    private lateinit var voiceNoteRecorder: VoiceNoteRecorder

    fun initRecorder(
        uiCallback: (state: Int) -> Unit,
        callbackUpdateDuration: (duration: Int) -> Unit
    ) {
        if (!this::voiceNoteRecorder.isInitialized) {
            voiceNoteRecorder = VoiceNoteRecorder()
        }
        voiceNoteRecorder.setCallback(uiCallback, callbackUpdateDuration)
    }

    fun getCurrentDayAndTime() {
        viewModelScope.launch {
            val day = LocalDate.now().toDayMonthYear()
            selectDay.value = day
            val time = LocalTime.now().toHourMinute()
            selectTime.value = time
        }
    }

    fun updateSelectDay(year: Int, month: Int, dayOfMonth: Int) {
        viewModelScope.launch {
            selectDay.value = "${dayOfMonth.formatDoubleChar()}/${month.formatDoubleChar()}/$year"
        }
    }

    fun updateSelectTime(hourOfDay: Int, minute: Int) {
        viewModelScope.launch {
            selectTime.value = "${hourOfDay.formatDoubleChar()}:${minute.formatDoubleChar()}"
        }
    }

    fun deleteAudioOldNote() {
        File(oldNote.audioPath.toString()).delete()
        oldNote.audioPath = ""
    }

    fun getCurrentDuration() = voiceNoteRecorder.getCurrentDuration()

    fun getStateOfRecorder(
        uiCallback: (state: Int) -> Unit
    ) {
        if (voiceNoteRecorder.isRecording and voiceNoteRecorder.isPaused) {
            uiCallback(PAUSE_RECORDING)
        }
    }

    fun onClickBtnRecord(context: Context) {
        when {
            voiceNoteRecorder.isPaused -> voiceNoteRecorder.resumeRecorder()
            voiceNoteRecorder.isRecording -> voiceNoteRecorder.pauseRecorder()
            else -> voiceNoteRecorder.startRecorder(context)
        }
    }

    fun pauseRecorder() {
        if (voiceNoteRecorder.isRecording) {
            voiceNoteRecorder.pauseRecorder()
        }
    }

    fun deleteRecord() {
        viewModelScope.launch {
            if (voiceNoteRecorder.outputCacheFile.isNotBlank()) {
                File(voiceNoteRecorder.outputCacheFile).delete()
                voiceNoteRecorder.outputCacheFile = ""
                voiceNoteRecorder.stopRecorder()
            }
        }
    }

    fun saveRecord(
        context: Context,
        callback: (path: String) -> Unit
    ) {
        viewModelScope.launch {
            if (voiceNoteRecorder.outputCacheFile.isNotBlank()) {
                voiceNoteRecorder.stopRecorder()
                val outputPersistentFile =
                    "${getPersistentRecordDirPath(context)}/record_${LocalDateTime.now().toMilli()}"
                val srcFile = File(voiceNoteRecorder.outputCacheFile)
                val dstFile = File(outputPersistentFile)
                copy(srcFile, dstFile)
                srcFile.delete()
                voiceNoteRecorder.outputCacheFile = ""
                callback(outputPersistentFile)
            } else {
                callback("")
            }
        }
    }

    fun isRecording() = voiceNoteRecorder.isRecording

    fun saveNoteToLocalDatabase(
        note: Note,
        callback: () -> Unit
    ) {
        // define primary key for note
        note.id = note.hashCode()
        viewModelScope.launch {
            if (noteMode == ADD_NOTE_MODE) {
                noteService.insertNote(note)
            } else if (noteMode == UPDATE_NOTE_MODE) {
                note.isDone = oldNote.isDone
                noteService.deleteNote(oldNote)
                noteService.insertNote(note)
                cancelAlarmForOldNote()
            }
            callback()
        }
    }

    fun refreshNotesDayMapInDataObject(
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            Data.getLocalNotesRuntime(noteService)
            callback()
        }
    }

    fun setAlarmForNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val isNotify = dataLocalManager.getIsNotifyEvents()
            if (isNotify and !note.isDone) {
                alarmEventsScheduler.scheduleEvent(note)
            }
        }
    }

    private fun cancelAlarmForOldNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val isNotify = dataLocalManager.getIsNotifyEvents()
            if (isNotify) {
                alarmEventsScheduler.cancelEvent(oldNote)
            }
        }
    }
}