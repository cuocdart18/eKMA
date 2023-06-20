package com.example.kmatool.data.service

import com.example.kmatool.common.Resource
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.repository.IAuth
import com.example.kmatool.data.models.service.ILoginService
import javax.inject.Inject

class LoginService @Inject constructor(
    private val dataLocalManager: IDataLocalManager,
    private val auth: IAuth
) : ILoginService {

    override suspend fun saveLoginState(data: Boolean) {
        dataLocalManager.saveLoginState(data)
    }

    override suspend fun getLoginState(): Boolean {
        return dataLocalManager.getLoginState()
    }

    override suspend fun auth(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<String> {
        return auth.auth(username, password, hashed)
    }
}