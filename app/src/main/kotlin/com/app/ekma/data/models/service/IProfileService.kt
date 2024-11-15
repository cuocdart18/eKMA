package com.app.ekma.data.models.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Profile
import com.app.ekma.data.models.ProfileDetail
import kotlinx.coroutines.flow.Flow

interface IProfileService {

    suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<Profile>

    suspend fun getProfileDetail(
        username: String,
        password: String,
        hashed: Boolean
    ): Flow<Resource<ProfileDetail>>

    suspend fun saveProfile(profile: Profile)

    suspend fun saveProfile(profile: ProfileDetail)

    suspend fun clearProfile(myStudentCode: String)

    suspend fun getProfile(): Profile

    suspend fun getProfileDetail(): Flow<ProfileDetail>

    suspend fun updateFcmToken()
}