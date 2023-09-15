package com.app.ekma.data.repository

import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.common.Resource
import com.app.ekma.data.data_source.apis.ScheduleAPI
import com.app.ekma.data.models.repository.IAuth
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