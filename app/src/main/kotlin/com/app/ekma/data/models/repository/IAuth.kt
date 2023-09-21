package com.app.ekma.data.models.repository

import com.app.ekma.common.Resource

interface IAuth {

    suspend fun auth(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<String>
}