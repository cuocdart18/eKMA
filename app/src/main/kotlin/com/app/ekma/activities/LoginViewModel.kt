package com.app.ekma.activities

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.app.ekma.alarm.AlarmEventsScheduler
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.AUTH_MESSAGE_SUCCESS
import com.app.ekma.common.Data
import com.app.ekma.common.Resource
import com.app.ekma.common.md5
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.User
import com.app.ekma.data.models.service.ILoginService
import com.app.ekma.data.models.service.INoteService
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.data.models.service.IUserService
import com.app.ekma.work.WorkRunner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginService: ILoginService,
    private val profileService: IProfileService,
    private val userService: IUserService,
    private val noteService: INoteService,
    private val alarmEventsScheduler: AlarmEventsScheduler,
    private val dataLocalManager: IDataLocalManager
) : BaseViewModel() {
    override val TAG = LoginViewModel::class.java.simpleName

    // observable field
    var isValid = ObservableField<Boolean>()
    var isShowProgress = ObservableField<Boolean>()

    init {
        isValid.set(true)
        isShowProgress.set(false)
    }

    fun handleOnClickBtnLogin(
        context: Context,
        username: String,
        unHashedPassword: String,
        callback: () -> Unit
    ) {
        isValid.set(true)
        if (username.isBlank() or unHashedPassword.isBlank()) {
            showLoginRefused()      // if invalid input
            return
        }
        // show for user: aware of request
        isShowProgress.set(true)
        val password = md5(unHashedPassword)
        viewModelScope.launch {
            val callAuth = loginService.auth(username, password, true)
            if (callAuth !is Resource.Success || !(callAuth.data.equals(AUTH_MESSAGE_SUCCESS))) {
                showLoginRefused()      // if get or auth failed
                return@launch
            }
            val profile = profileService.getProfile(username, password, true)
            if (profile !is Resource.Success || profile.data == null) {
                showLoginRefused()      // if backend unavailable
                return@launch
            }
            val myStudentCode = profile.data.studentCode
            // save data
            profileService.saveProfile(profile.data)
            userService.saveUser(User(username, password, true))
            loginService.saveLoginState(true)
            // handle get remote data
            runGetDataWorker(context, myStudentCode)
            // get notes and set alarm
            setAlarmForNotes()
            // update UI
            showLoginAccept()
            callback()
        }
    }

    private fun runGetDataWorker(context: Context, myStudentCode: String) {
        val workManager = WorkManager.getInstance(context)
        WorkRunner.runGetScheduleWorker(workManager)
        WorkRunner.runDownloadAvatarWorker(workManager, myStudentCode)
        WorkRunner.runDownloadAudioNotesWorker(workManager, myStudentCode)
    }

    private suspend fun setAlarmForNotes() = withContext(Dispatchers.IO) {
        Data.getLocalNotesRuntime(noteService)
        val isNotify = dataLocalManager.getIsNotifyEvents()
        if (isNotify) {
            alarmEventsScheduler.scheduleNotes()
        }
    }

    private fun showLoginAccept() {
        isValid.set(true)
        isShowProgress.set(false)
    }

    private fun showLoginRefused() {
        isValid.set(false)
        isShowProgress.set(false)
    }
}