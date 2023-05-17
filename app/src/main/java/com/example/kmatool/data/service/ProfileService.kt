package com.example.kmatool.data.service

import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.repository.IProfileRepository
import com.example.kmatool.data.models.service.IProfileService
import javax.inject.Inject

class ProfileService @Inject constructor(
    private val profileRepository: IProfileRepository
) : IProfileService {

    override suspend fun getProfile(username: String, password: String, hashed: Boolean): Profile {
        return profileRepository.getProfile(username, password, hashed)
    }

    override suspend fun saveProfile(profile: Profile) {
        profileRepository.saveProfile(profile)
    }

    override suspend fun clearProfile() {
        profileRepository.clearProfile()
    }

    override suspend fun getProfile(): String {
        return profileRepository.getProfile()
    }
}