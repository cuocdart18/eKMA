package com.app.ekma.data.models.service

import com.app.ekma.data.models.User

interface IUserService {

    suspend fun saveUser(user: User)

    suspend fun clearUser()

    suspend fun getUser(): User
}