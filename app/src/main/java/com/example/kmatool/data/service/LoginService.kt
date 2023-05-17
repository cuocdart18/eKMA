package com.example.kmatool.data.service

import com.example.kmatool.data.data_source.app_data.DataLocalManager
import com.example.kmatool.data.models.service.ILoginService
import javax.inject.Inject

class LoginService @Inject constructor(
    private val dataLocalManager: DataLocalManager
) : ILoginService {

    override suspend fun saveLoginState(data: Boolean) {
        dataLocalManager.saveLoginStateSPref(data)
    }

    override suspend fun getLoginState(): Boolean {
        return dataLocalManager.getLoginStateSPref()
    }
}