package com.app.ekma.data.models.repository

import com.app.ekma.data.models.User

interface IUserRepository {

    suspend fun saveUser(user: User)

    suspend fun getUser(): User

    suspend fun clearUser()
}