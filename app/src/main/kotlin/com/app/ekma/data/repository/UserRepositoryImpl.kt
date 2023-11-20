package com.app.ekma.data.repository

import com.app.ekma.base.repositories.BaseRepositories
import com.app.ekma.common.jsonStringToObject
import com.app.ekma.data.data_source.app_data.IDataLocalManager
import com.app.ekma.data.models.User
import com.app.ekma.data.models.repository.IUserRepository
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