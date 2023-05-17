package com.example.kmatool.data.models.repository

import com.example.kmatool.data.models.User

interface IUserRepository {

    suspend fun saveUser(user: User)

    suspend fun getUser(): User

    suspend fun clearUser()
}