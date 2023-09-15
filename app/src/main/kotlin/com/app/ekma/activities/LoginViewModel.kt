package com.app.ekma.activities

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.AUTH_MESSAGE_SUCCESS
import com.app.ekma.common.Resource
import com.app.ekma.common.md5
import com.app.ekma.data.models.User
import com.app.ekma.data.models.service.ILoginService
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.data.models.service.IUserService
import com.app.ekma.work.GetScheduleWorkRunner
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
            viewModelScope.launch {
                val callAuth = loginService.auth(username, password, true)
                if (callAuth is Resource.Success && callAuth.data.equals(AUTH_MESSAGE_SUCCESS)) {
                    val profile = profileService.getProfile(username, password, true)
                    if (profile is Resource.Success && profile.data != null) {
                        // save data to local
                        profileService.saveProfile(profile.data)
                        userService.saveUser(User(username, password, true))
                        loginService.saveLoginState(true)
                        // handle schedule
                        runGetScheduleWorker(context)
                        showLoginAccept()
                        callback()
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