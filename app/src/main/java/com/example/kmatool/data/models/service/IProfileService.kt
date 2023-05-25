package com.example.kmatool.data.models.service

import com.example.kmatool.common.Resource
import com.example.kmatool.data.models.Profile

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