package com.app.ekma.data.models.repository

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Profile
import com.app.ekma.data.models.ProfileDetail

interface IProfileRepository {

    suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<Profile>

    suspend fun getProfileDetail(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<ProfileDetail>

    suspend fun saveProfile(profile: Profile)

    suspend fun saveProfile(profile: ProfileDetail)

    suspend fun saveProfileToFirestore(profile: Profile)

    suspend fun clearProfile()

    suspend fun clearFcmToken(myStudentCode: String)

    suspend fun getFcmToken(code: String): String

    suspend fun updateFcmTokenToFirestore(myStudentCode: String)

    suspend fun getProfile(): Profile

    suspend fun getProfileDetail(): ProfileDetail?
}