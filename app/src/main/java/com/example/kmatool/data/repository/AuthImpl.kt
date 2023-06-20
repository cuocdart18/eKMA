package com.example.kmatool.data.repository

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.common.Resource
import com.example.kmatool.data.data_source.apis.ScheduleAPI
import com.example.kmatool.data.models.repository.IAuth
import javax.inject.Inject

class AuthImpl @Inject constructor(
    private val scheduleAPI: ScheduleAPI
) : BaseRepositories(), IAuth {

    override suspend fun auth(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<String> {
        return safeApiCall {
            scheduleAPI.auth(username, password, hashed).toMessage()
        }
    }
}