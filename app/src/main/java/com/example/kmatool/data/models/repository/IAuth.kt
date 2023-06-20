package com.example.kmatool.data.models.repository

import com.example.kmatool.common.Resource

interface IAuth {

    suspend fun auth(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<String>
}