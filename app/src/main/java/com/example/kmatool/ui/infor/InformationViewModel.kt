package com.example.kmatool.ui.infor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.Data
import com.example.kmatool.common.DataStoreManager
import com.example.kmatool.common.jsonStringToObject
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.repositories.NoteRepository
import com.example.kmatool.data.repositories.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val noteRepository: NoteRepository,
    private val scheduleRepository: ScheduleRepository
) : BaseViewModel() {
    override val TAG = InformationViewModel::class.java.simpleName

    private val _profile = MutableLiveData<Profile>()
    val profile: LiveData<Profile> = _profile

    init {
        getProfile()
    }

    private fun getProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.profileDataStoreFlow.collect() {
                val parsedProfile = jsonStringToObject<Profile>(it)
                withContext(Dispatchers.Main) {
                    _profile.value = parsedProfile
                    logDebug("get profile=${_profile.value}")
                }
                cancel()
            }
        }
    }

    fun storeIsNotifyEvents(
        data: Boolean,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.storeIsNotifyEvents(data)
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun signOut(
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val clearPeriods = launch { scheduleRepository.deletePeriods { } }
            val clearNotes = launch { noteRepository.deleteNotes { } }
            val clearProfile = launch { dataStoreManager.storeProfile("") }
            val clearNotifyEvent = launch { dataStoreManager.storeIsNotifyEvents(false) }
            val clearLoginState = launch { dataStoreManager.storeIsLogin(false) }
            val clearDataRuntime = launch {
                Data.notesDayMap.clear()
                Data.periodsDayMap.clear()
            }

            clearPeriods.join()
            clearNotes.join()
            clearProfile.join()
            clearNotifyEvent.join()
            clearLoginState.join()
            clearDataRuntime.join()

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}