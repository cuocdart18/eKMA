package com.example.kmatool.activities

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.app_data.DataLocalManager
import com.example.kmatool.data.repositories.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataLocalManager: DataLocalManager,
    private val scheduleRepository: ScheduleRepository
) : BaseViewModel() {
    override val TAG = LoginViewModel::class.java.simpleName
    private val DELAY_TIME = 1200L

    // observable field
    var isValid = ObservableField<Boolean>()
    var isShowProgress = ObservableField<Boolean>()

    init {
        isValid.set(true)
        isShowProgress.set(false)
    }

    fun getLoginState(
        callback: () -> Unit
    ) {
        logDebug("get login state")
        isShowProgress.set(true)
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.getLoginState { state ->
                CoroutineScope(Dispatchers.Main).launch {
                    delay(DELAY_TIME)
                    if (state) {
                        callback()
                    }
                    isShowProgress.set(false)
                }
            }
        }
    }

    fun getLocalData(
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.getLocalData()
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun handleOnClickBtnLogin(
        username: String,
        password: String,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataLocalManager.saveUsername(username)
            dataLocalManager.savePassword(password)
        }
        logInfo("handle input")
        isShowProgress.set(true)
        isValid.set(true)
        // action
        if (username.isNotBlank() && password.isNotBlank()) {
            logDebug("valid input")
            viewModelScope.launch(Dispatchers.IO) {
                // call profile
                val profileCallState =
                    async { scheduleRepository.callProfileApi(username, password) }
                // call schedule -> set alarm if possible
                val scheduleCallState =
                    async { scheduleRepository.callScheduleApi(username, password) }

                if (profileCallState.await() && scheduleCallState.await()) {
                    scheduleRepository.saveLoginStateToLocal(true) {
                        logDebug("save login state successfully")
                    }.join()
                    withContext(Dispatchers.Main) {
                        isValid.set(true)
                        isShowProgress.set(false)
                        logDebug("handle valid response from Api")
                        callback()
                    }
                } else {
                    logDebug("login denied")
                    isValid.set(false)
                    isShowProgress.set(false)
                }
            }
        } else {
            logDebug("invalid input")
            isValid.set(false)
            isShowProgress.set(false)
        }
    }
}