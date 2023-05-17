package com.example.kmatool.data.models.repository

import com.example.kmatool.data.models.Profile

interface IProfileRepository {

    suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Profile

    suspend fun saveProfile(profile: Profile)

    suspend fun clearProfile()

    suspend fun getProfile(): Profile
}