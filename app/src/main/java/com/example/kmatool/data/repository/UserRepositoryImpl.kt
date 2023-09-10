package com.example.kmatool.data.repository

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.common.jsonStringToObject
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.User
import com.example.kmatool.data.models.repository.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataLocalManager: IDataLocalManager
) : BaseRepositories(), IUserRepository {

    override suspend fun saveUser(user: User) {
        dataLocalManager.saveUser(user.toUserShPref())
    }

    override suspend fun clearUser() {
        dataLocalManager.saveUser("")
    }

    override suspend fun getUser(): User {
        return withContext(Dispatchers.Default) {
            jsonStringToObject(dataLocalManager.getUser())
        }
    }
}