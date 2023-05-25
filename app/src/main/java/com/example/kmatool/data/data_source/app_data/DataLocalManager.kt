package com.example.kmatool.data.data_source.app_data

import javax.inject.Singleton

@Singleton
class DataLocalManager (
    private val mySharePreferences: MySharePreferences,
    private val dataStoreManager: DataStoreManager
) : IDataLocalManager {
    private val KEY_LOGIN_STATE = "login_state_sPref"
    private val KEY_IMG_PATH = "img_path_sPref"
    private val KEY_PROFILE = "profile_sPref"
    private val KEY_NOTIFY_EVENTS = "notify_events_sPref"
    private val KEY_USERNAME = "username_sPref"
    private val KEY_PASSWORD = "password_sPref"
    private val KEY_USER = "user_sPref"

    override suspend fun saveUser(data: String) {
        mySharePreferences.putStringValueEncrypted(KEY_USER, data)
    }

    override suspend fun getUser(): String {
        return mySharePreferences.getStringValueEncrypted(KEY_USER).toString()
    }

    override suspend fun savePassword(data: String) {
        mySharePreferences.putStringValueEncrypted(KEY_PASSWORD, data)
    }

    override suspend fun getPassword(): String {
        return mySharePreferences.getStringValueEncrypted(KEY_PASSWORD).toString()
    }

    override suspend fun saveUsername(data: String) {
        mySharePreferences.putStringValueEncrypted(KEY_USERNAME, data)
    }

    override suspend fun getUsername(): String {
        return mySharePreferences.getStringValueEncrypted(KEY_USERNAME).toString()
    }

    override suspend fun saveImgFilePath(data: String) {
        mySharePreferences.putStringValue(KEY_IMG_PATH, data)
    }

    override suspend fun getImgFilePath(): String {
        return mySharePreferences.getStringValue(KEY_IMG_PATH).toString()
    }

    override suspend fun saveLoginState(isLogin: Boolean) {
        mySharePreferences.putBooleanValueEncrypted(KEY_LOGIN_STATE, isLogin)
    }

    override suspend fun getLoginState(): Boolean {
        return mySharePreferences.getBooleanValueEncrypted(KEY_LOGIN_STATE)
    }

    override suspend fun saveProfile(data: String) {
        mySharePreferences.putStringValueEncrypted(KEY_PROFILE, data)
    }

    override suspend fun getProfile(): String {
        return mySharePreferences.getStringValueEncrypted(KEY_PROFILE).toString()
    }

    override suspend fun saveIsNotifyEvents(data: Boolean) {
        mySharePreferences.putBooleanValue(KEY_NOTIFY_EVENTS, data)
    }

    override suspend fun getIsNotifyEvents(): Boolean {
        return mySharePreferences.getBooleanValue(KEY_NOTIFY_EVENTS)
    }
}