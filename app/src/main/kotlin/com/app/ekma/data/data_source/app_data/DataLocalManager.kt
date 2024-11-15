package com.app.ekma.data.data_source.app_data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class DataLocalManager(
    private val mySharePreferences: MySharePreferences,
    private val dataStoreManager: DataStoreManager
) : IDataLocalManager {
    private val KEY_LOGIN_STATE = "login_state_sPref"
    private val KEY_IMG_PATH = "img_path_sPref"
    private val KEY_PROFILE = "profile_sPref"
    private val KEY_PROFILE_DETAIL = "profile_detail_sPref"
    private val KEY_NOTIFY_EVENTS = "notify_events_sPref"
    private val KEY_USERNAME = "username_sPref"
    private val KEY_PASSWORD = "password_sPref"
    private val KEY_USER = "user_sPref"

    override suspend fun saveUser(data: String) = withContext(Dispatchers.IO) {
        mySharePreferences.putStringValueEncrypted(KEY_USER, data)
    }

    override suspend fun getUser(): String {
        return withContext(Dispatchers.IO) {
            mySharePreferences.getStringValueEncrypted(KEY_USER).toString()
        }
    }

    override suspend fun savePassword(data: String) = withContext(Dispatchers.IO) {
        mySharePreferences.putStringValueEncrypted(KEY_PASSWORD, data)
    }

    override suspend fun getPassword(): String {
        return withContext(Dispatchers.IO) {
            mySharePreferences.getStringValueEncrypted(KEY_PASSWORD).toString()
        }
    }

    override suspend fun saveUsername(data: String) = withContext(Dispatchers.IO) {
        mySharePreferences.putStringValueEncrypted(KEY_USERNAME, data)
    }

    override suspend fun getUsername(): String {
        return withContext(Dispatchers.IO) {
            mySharePreferences.getStringValueEncrypted(KEY_USERNAME).toString()
        }
    }

    override suspend fun saveImgFilePath(data: String) = withContext(Dispatchers.IO) {
        mySharePreferences.putStringValue(KEY_IMG_PATH, data)
    }

    override suspend fun getImgFilePath(): String {
        return withContext(Dispatchers.IO) {
            mySharePreferences.getStringValue(KEY_IMG_PATH).toString()
        }
    }

    override suspend fun saveLoginState(isLogin: Boolean) = withContext(Dispatchers.IO) {
        mySharePreferences.putBooleanValueEncrypted(KEY_LOGIN_STATE, isLogin)
    }

    override suspend fun getLoginState(): Boolean {
        return withContext(Dispatchers.IO) {
            mySharePreferences.getBooleanValueEncrypted(KEY_LOGIN_STATE)
        }
    }

    override suspend fun saveProfile(data: String) = withContext(Dispatchers.IO) {
        mySharePreferences.putStringValueEncrypted(KEY_PROFILE, data)
    }

    override suspend fun getProfile(): String {
        return withContext(Dispatchers.IO) {
            mySharePreferences.getStringValueEncrypted(KEY_PROFILE).toString()
        }
    }

    override suspend fun saveProfileDetail(data: String) = withContext(Dispatchers.IO) {
        mySharePreferences.putStringValueEncrypted(KEY_PROFILE_DETAIL, data)
    }

    override suspend fun getProfileDetail(): String {
        return withContext(Dispatchers.IO) {
            mySharePreferences.getStringValueEncrypted(KEY_PROFILE_DETAIL).toString()
        }
    }

    override suspend fun saveIsNotifyEvents(data: Boolean) = withContext(Dispatchers.IO) {
        mySharePreferences.putBooleanValue(KEY_NOTIFY_EVENTS, data)
    }

    override suspend fun getIsNotifyEvents(): Boolean {
        return withContext(Dispatchers.IO) {
            mySharePreferences.getBooleanValue(KEY_NOTIFY_EVENTS)
        }
    }
}