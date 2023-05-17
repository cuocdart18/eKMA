package com.example.kmatool.data.data_source.app_data

import javax.inject.Singleton

@Singleton
class DataLocalManager(
    private val mySharePreferences: MySharePreferences,
    private val dataStoreManager: DataStoreManager
) {
    private val KEY_LOGIN_STATE = "login_state_sPref"
    private val KEY_IMG_PATH = "img_path_sPref"
    private val KEY_PROFILE = "profile_sPref"
    private val KEY_NOTIFY_EVENTS = "notify_events_sPref"
    private val KEY_USERNAME = "username_sPref"
    private val KEY_PASSWORD = "password_sPref"

    suspend fun savePassword(data: String) {
        mySharePreferences.putStringValueEncrypted(KEY_PASSWORD, data)
    }

    suspend fun getPassword(): String {
        val data = mySharePreferences.getStringValueEncrypted(KEY_PASSWORD).toString()
        return data
    }

    suspend fun saveUsername(data: String) {
        mySharePreferences.putStringValueEncrypted(KEY_USERNAME, data)
    }

    suspend fun getUsername(): String {
        val data = mySharePreferences.getStringValueEncrypted(KEY_USERNAME).toString()
        return data
    }

    suspend fun saveImgFilePathSPref(data: String) {
        mySharePreferences.putStringValue(KEY_IMG_PATH, data)
    }

    suspend fun getImgFilePathSPref(): String {
        val data = mySharePreferences.getStringValue(KEY_IMG_PATH).toString()
        return data
    }

    suspend fun saveLoginStateSPref(isLogin: Boolean) {
        mySharePreferences.putBooleanValueEncrypted(KEY_LOGIN_STATE, isLogin)
    }

    suspend fun getLoginStateSPref(): Boolean {
        val data = mySharePreferences.getBooleanValueEncrypted(KEY_LOGIN_STATE)
        return data
    }

    suspend fun saveProfileSPref(data: String) {
        mySharePreferences.putStringValueEncrypted(KEY_PROFILE, data)
    }

    suspend fun getProfileSPref(): String {
        val data = mySharePreferences.getStringValueEncrypted(KEY_PROFILE).toString()
        return data
    }

    suspend fun saveIsNotifyEventsSPref(data: Boolean) {
        mySharePreferences.putBooleanValue(KEY_NOTIFY_EVENTS, data)
    }

    suspend fun getIsNotifyEventsSPref(): Boolean {
        val data = mySharePreferences.getBooleanValue(KEY_NOTIFY_EVENTS)
        return data
    }

    suspend fun saveProfile(data: String) {
        dataStoreManager.storeProfile(data)
    }

    suspend fun getProfile(
        callback: (data: String) -> Unit
    ) {
        dataStoreManager.profileDataStoreFlow.collect() {
            callback(it)
        }
    }

    suspend fun saveImgFilePath(data: String) {
        dataStoreManager.storeImgFilePath(data)
    }

    suspend fun getImgFilePath(
        callback: (data: String) -> Unit
    ) {
        dataStoreManager.imgFilePathDataStoreFlow.collect() {
            callback(it)
        }
    }

    suspend fun saveIsLogin(data: Boolean) {
        dataStoreManager.storeIsLogin(data)
    }

    suspend fun getIsLogin(
        callback: (data: Boolean) -> Unit
    ) {
        dataStoreManager.isLoginDataStoreFlow.collect() {
            callback(it)
        }
    }

    suspend fun saveIsNotifyEvents(data: Boolean) {
        dataStoreManager.storeIsNotifyEvents(data)
    }

    suspend fun getIsNotifyEvents(
        callback: (data: Boolean) -> Unit
    ) {
        dataStoreManager.isNotifyEventsDataStoreFlow.collect() {
            callback(it)
        }
    }
}