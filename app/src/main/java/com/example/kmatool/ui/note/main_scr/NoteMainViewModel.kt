package com.example.kmatool.ui.note.main_scr

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.formatDoubleChar
import com.example.kmatool.common.toDayMonthYear
import com.example.kmatool.common.toHourMinute
import com.example.kmatool.data.models.Note
import com.example.kmatool.data.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class NoteMainViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : BaseViewModel() {
    override val TAG: String = NoteMainViewModel::class.java.simpleName
    val selectDay = MutableLiveData<String>()
    val selectTime = MutableLiveData<String>()

    init {
        getCurrentDayAndTime()
    }

    private fun getCurrentDayAndTime() {
        logDebug("get current day and time of device")
        viewModelScope.launch(Dispatchers.Main) {
            val day = LocalDate.now().toDayMonthYear()
            selectDay.value = day
            val time = LocalTime.now().toHourMinute()
            selectTime.value = time
        }
    }

    fun updateSelectDay(year: Int, month: Int, dayOfMonth: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            selectDay.value = "${dayOfMonth.formatDoubleChar()}/${month.formatDoubleChar()}/$year"
        }
    }

    fun updateSelectTime(hourOfDay: Int, minute: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            selectTime.value = "${hourOfDay.formatDoubleChar()}:${minute.formatDoubleChar()}"
        }
    }

    fun saveNoteToLocalDatabase(
        note: Note,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.insertNote(note) {
                CoroutineScope(Dispatchers.Main).launch {
                    // on success
                    callback()
                }
            }
        }
    }

    fun refreshNotesDayMapInDataObject(
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateLocalDataRuntime()
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}