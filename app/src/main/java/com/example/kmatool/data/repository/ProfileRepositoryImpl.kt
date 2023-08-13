package com.example.kmatool.data.repository

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.common.Data
import com.example.kmatool.common.KEY_STUDENT_PROFILE
import com.example.kmatool.common.KEY_USERS_COLL
import com.example.kmatool.common.KEY_USER_DOB
import com.example.kmatool.common.KEY_USER_GENDER
import com.example.kmatool.common.KEY_USER_ID
import com.example.kmatool.common.KEY_USER_NAME
import com.example.kmatool.common.Resource
import com.example.kmatool.common.jsonStringToObject
import com.example.kmatool.data.data_source.apis.ScheduleAPI
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.repository.IProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val scheduleAPI: ScheduleAPI,
    private val dataLocalManager: IDataLocalManager
) : BaseRepositories(), IProfileRepository {

    override suspend fun getProfile(
        username: String,
        password: String,
        hashed: Boolean
    ): Resource<Profile> {
        return safeApiCall {
            scheduleAPI.getProfile(username, password, hashed).toProfile()
        }
    }

    override suspend fun saveProfile(profile: Profile) {
        dataLocalManager.saveProfile(profile.toProfileShPref())
    }

    override suspend fun saveProfileToFirestore(profile: Profile) {
        val profileMap = mapOf(
            KEY_USER_ID to profile.studentCode,
            KEY_USER_NAME to profile.displayName,
            KEY_USER_DOB to profile.birthday,
            KEY_USER_GENDER to profile.gender
        )
        Data.firestore.collection(KEY_USERS_COLL)
            .document(profile.studentCode)
            .set(profileMap)
    }

    override suspend fun clearProfile() {
        dataLocalManager.saveProfile("")
    }

    override suspend fun getProfile(): Profile {
        return jsonStringToObject(dataLocalManager.getProfile())
    }
}