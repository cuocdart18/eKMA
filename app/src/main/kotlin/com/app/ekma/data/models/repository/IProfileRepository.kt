package com.app.ekma.data.models.repository

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Profile

interface IProfileRepository {

    suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<Profile>

    suspend fun saveProfile(profile: Profile)

    suspend fun saveProfileToFirestore(profile: Profile)

    suspend fun setActiveStatus(status: String)

    suspend fun clearProfile()

    suspend fun clearFcmToken()

    suspend fun getFcmToken(code: String): String

    suspend fun getProfile(): Profile
}