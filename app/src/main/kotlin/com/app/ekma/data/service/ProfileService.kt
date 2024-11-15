package com.app.ekma.data.service

import com.app.ekma.common.Resource
import com.app.ekma.data.models.Profile
import com.app.ekma.data.models.ProfileDetail
import com.app.ekma.data.models.repository.IProfileRepository
import com.app.ekma.data.models.repository.IUserRepository
import com.app.ekma.data.models.service.IProfileService
import com.app.ekma.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfileService @Inject constructor(
    private val profileRepository: IProfileRepository,
    private val userRepository: IUserRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : IProfileService {

    override suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<Profile> {
        return profileRepository.getProfile(username, password, hashed)
    }

    override suspend fun getProfileDetail(
        username: String,
        password: String,
        hashed: Boolean
    ): Flow<Resource<ProfileDetail>> = channelFlow {
        send(profileRepository.getProfileDetail(username, password, hashed))
    }.flowOn(dispatcher)

    override suspend fun saveProfile(profile: Profile) {
        profileRepository.saveProfile(profile)
        profileRepository.saveProfileToFirestore(profile)
    }

    override suspend fun saveProfile(profile: ProfileDetail) {
        profileRepository.saveProfile(profile)
    }

    override suspend fun clearProfile(myStudentCode: String) {
        profileRepository.clearFcmToken(myStudentCode)
        profileRepository.clearProfile()
    }

    override suspend fun getProfile(): Profile {
        return profileRepository.getProfile()
    }

    override suspend fun getProfileDetail(): Flow<ProfileDetail> = channelFlow {
        val res = profileRepository.getProfileDetail()
        if (res == null) {
            val user = userRepository.getUser()
            getProfileDetail(user.username, user.password, true).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { data ->
                            saveProfile(data)
                            send(data)
                        }
                    }

                    is Resource.Error -> {
                    }
                }
            }
        } else {
            send(res)
        }
    }.flowOn(dispatcher)

    override suspend fun updateFcmToken() {
        val myStudentCode = getProfile().studentCode
        profileRepository.updateFcmTokenToFirestore(myStudentCode)
    }
}