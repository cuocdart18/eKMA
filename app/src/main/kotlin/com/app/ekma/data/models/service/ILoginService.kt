package com.app.ekma.data.models.service

import com.app.ekma.common.Resource


interface ILoginService {

    suspend fun saveLoginState(data: Boolean)

    suspend fun getLoginState(): Boolean

    suspend fun auth(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<String>
}