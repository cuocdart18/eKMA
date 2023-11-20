package com.app.ekma.data.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Profile
import com.app.ekma.data.models.repository.IProfileRepository
import com.app.ekma.data.models.service.IProfileService
import javax.inject.Inject

class ProfileService @Inject constructor(
    private val profileRepository: IProfileRepository
) : IProfileService {

    override suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<Profile> {
        return profileRepository.getProfile(username, password, hashed)
    }

    override suspend fun saveProfile(profile: Profile) {
        profileRepository.saveProfile(profile)
        profileRepository.saveProfileToFirestore(profile)
    }

    override suspend fun clearProfile(myStudentCode: String) {
        profileRepository.clearFcmToken(myStudentCode)
        profileRepository.clearProfile()
    }

    override suspend fun getProfile(): Profile {
        return profileRepository.getProfile()
    }

    override suspend fun updateFcmToken() {
        val myStudentCode = getProfile().studentCode
        profileRepository.updateFcmTokenToFirestore(myStudentCode)
    }
}