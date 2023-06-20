package com.example.kmatool.activities

import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.models.service.ILoginService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loginService: ILoginService
) : BaseViewModel() {
    override val TAG = SplashViewModel::class.java.simpleName

    fun getLoginState(
        callback: (state: Boolean) -> Unit
    ) {
        logDebug("get login state")
        viewModelScope.launch(Dispatchers.IO) {
            delay(1500L)
            val state = loginService.getLoginState()
            withContext(Dispatchers.Main) {
                callback(state)
            }
        }
    }
}