package com.app.ekma.data.models.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Profile

interface IProfileService {

    suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<Profile>

    suspend fun saveProfile(profile: Profile)

    suspend fun clearProfile()

    suspend fun getProfile(): Profile
}