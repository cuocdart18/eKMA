package com.example.kmatool.data.service

import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.service.ILoginService
import javax.inject.Inject

class LoginService @Inject constructor(
    private val dataLocalManager: IDataLocalManager
) : ILoginService {

    override suspend fun saveLoginState(data: Boolean) {
        dataLocalManager.saveLoginState(data)
    }

    override suspend fun getLoginState(): Boolean {
        return dataLocalManager.getLoginState()
    }
}