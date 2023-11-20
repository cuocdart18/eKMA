package com.app.ekma.data.service

import com.app.ekma.data.models.User
import com.app.ekma.data.models.repository.IUserRepository
import com.app.ekma.data.models.service.IUserService
import javax.inject.Inject

class UserService @Inject constructor(
    private val userRepository: IUserRepository
) : IUserService {

    override suspend fun saveUser(user: User) {
        userRepository.saveUser(user)
    }

    override suspend fun clearUser() {
        userRepository.clearUser()
    }

    override suspend fun getUser(): User {
        return userRepository.getUser()
    }
}