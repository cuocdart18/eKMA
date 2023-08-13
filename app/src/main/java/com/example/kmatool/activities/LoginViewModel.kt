package com.example.kmatool.activities

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.common.AUTH_MESSAGE_SUCCESS
import com.example.kmatool.common.GET_SCHEDULE_WORKER_TAG
import com.example.kmatool.common.Resource.Success
import com.example.kmatool.common.UNIQUE_GET_SCHEDULE_WORK_NAME
import com.example.kmatool.common.md5
import com.example.kmatool.data.models.User
import com.example.kmatool.data.models.service.ILoginService
import com.example.kmatool.data.models.service.IProfileService
import com.example.kmatool.data.models.service.IUserService
import com.example.kmatool.work.GetScheduleWorkRunner
import com.example.kmatool.work.GetScheduleWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginService: ILoginService,
    private val profileService: IProfileService,
    private val userService: IUserService
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
        if (username.isNotBlank() && unHashedPassword.isNotBlank()) {
            // show for user: aware of request
            isShowProgress.set(true)

            val password = md5(unHashedPassword)
            viewModelScope.launch(Dispatchers.IO) {
                val callAuth = loginService.auth(username, password, true)
                if (callAuth is Success && callAuth.data.equals(AUTH_MESSAGE_SUCCESS)) {
                    val profile = profileService.getProfile(username, password, true)
                    if (profile is Success && profile.data != null) {
                        profileService.saveProfile(profile.data)
                        // save hashed data to local
                        userService.saveUser(User(username, password, true))
                        loginService.saveLoginState(true)
                        // handle schedule
                        runGetScheduleWorker(context)
                        withContext(Dispatchers.Main) {
                            showLoginAccept()
                            callback()
                        }
                    } else {
                        showLoginRefused()      // if backend unavailable
                    }
                } else {
                    showLoginRefused()      // if get or auth failed
                }
            }
        } else {
            showLoginRefused()      // if invalid input
        }
    }

    private fun runGetScheduleWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)
        GetScheduleWorkRunner.run(workManager)
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