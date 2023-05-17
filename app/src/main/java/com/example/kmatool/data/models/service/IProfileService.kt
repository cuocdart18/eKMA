package com.example.kmatool.data.models.service

import com.example.kmatool.data.models.Profile

interface IProfileService {

    suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Profile

    suspend fun saveProfile(profile: Profile)

    suspend fun clearProfile()

    suspend fun getProfile(): String
}