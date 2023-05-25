package com.example.kmatool.data.models.service

interface ILoginService {

    suspend fun saveLoginState(data: Boolean)

    suspend fun getLoginState(): Boolean
}