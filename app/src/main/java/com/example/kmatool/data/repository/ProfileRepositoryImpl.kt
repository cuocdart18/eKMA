package com.example.kmatool.data.repository

import com.example.kmatool.common.jsonStringToObject
import com.example.kmatool.data.data_source.apis.ScheduleAPI
import com.example.kmatool.data.data_source.app_data.IDataLocalManager
import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.repository.IProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val scheduleAPI: ScheduleAPI,
    private val dataLocalManager: IDataLocalManager
) : IProfileRepository {

    override suspend fun getProfile(username: String, password: String, hashed: Boolean): Profile {
        return scheduleAPI.getProfile(username, password, hashed).toProfile()
    }

    override suspend fun saveProfile(profile: Profile) {
        dataLocalManager.saveProfile(profile.toProfileShPref())
    }

    override suspend fun clearProfile() {
        dataLocalManager.saveProfile("")
    }

    override suspend fun getProfile(): Profile {
        return jsonStringToObject(dataLocalManager.getProfile())
    }
}