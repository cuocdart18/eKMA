package com.example.kmatool.data.models.service

import com.example.kmatool.data.models.User

interface IUserService {

    suspend fun saveUser(user: User)

    suspend fun clearUser()

    suspend fun getUser(): User
}